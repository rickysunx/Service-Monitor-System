package com.renren.wan.monitor.dao;

import java.util.List;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import com.renren.wan.monitor.data.TableListData;
import com.renren.wan.monitor.entities.LogData;

@DAO(catalog="logcenter")
public interface LogCenterDAO {
	@SQL("select table_name from log_center.table_list where enable=1 order by table_name")
	public List<TableListData> getTables();
	//查看表是否存在
	@SQL("select count(1)>0 from information_schema.tables where table_name = :1")
	public boolean tableExists(String tableName);
	
	//查询日志
	@SQL("select * from ##(:1) WHERE ##(:2)  limit :3,:4")
	public List<LogData> getLogData(String tableName,String queryCond,int start,int limit);
}
