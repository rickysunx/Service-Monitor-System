package com.renren.wan.monitor.dao;

import java.sql.Timestamp;
import java.util.List;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;

import com.renren.wan.monitor.data.ReportProcessData;

@DAO
public interface ReportDAO {
	
	/**
	 * 查询处理数据
	 * @param start
	 * @param end
	 * @return
	 */
	@SQL("SELECT createTime startTime,processedTime endTime,indi.moduleId,m.moduleName, " +
		"plog.indicatorId,indi.indicatorName,plog.processedFlag,plog.logText,u.userId,u.userName " +
	"FROM t_indicator_process_log plog,t_indicator indi,t_module m,t_user u " +
	"WHERE plog.indicatorId=indi.indicatorId AND indi.moduleId=m.moduleId AND u.userId=plog.userId " +
		"AND plog.processedTime>=:1 " +
    	"AND plog.createTime<=:2 " +
    	"AND plog.userId=u.userId ")
	public List<ReportProcessData> queryProcessData(Timestamp start,Timestamp end);
	
}
