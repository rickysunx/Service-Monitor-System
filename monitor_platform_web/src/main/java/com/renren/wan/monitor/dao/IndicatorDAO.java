package com.renren.wan.monitor.dao;

import java.sql.Timestamp;
import java.util.List;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;

import com.renren.wan.monitor.data.IndicatorData;
import com.renren.wan.monitor.data.IndicatorStatusData;
import com.renren.wan.monitor.entities.TIndicator;
import com.renren.wan.monitor.entities.TIndicatorProcessLog;
import com.renren.wan.monitor.entities.TIndicatorStatus;
import com.renren.wan.monitor.entities.TUrlTestData;

@DAO
public interface IndicatorDAO {
	
	@SQL("select * from t_indicator where indicatorId=:1 ")
	public TIndicator getById(int indicatorId);
	
	@SQL("select * from t_indicator order by indicatorName")
	public List<TIndicator> getAll();
	
	@SQL("insert into t_indicator(indicatorName,moduleId,indicatorType,tableName,sampleRate," +
			"filterCond,errorCond,statTime,errorType,warnOperType,alertOperType,warnValue," +
			"alertValue,alertEnabled,enabled,urlMethod,urlCharset,urlPage,urlHeader,urlPostData,urlTimeout,minAlertValue)" +
			" values (:1.indicatorName,:1.moduleId,:1.indicatorType,:1.tableName,:1.sampleRate," +
			":1.filterCond,:1.errorCond,:1.statTime,:1.errorType,:1.warnOperType,:1.alertOperType," +
			":1.warnValue,:1.alertValue,:1.alertEnabled,:1.enabled,:1.urlMethod,:1.urlCharset,:1.urlPage," +
			":1.urlHeader,:1.urlPostData,:1.urlTimeout,:1.minAlertValue) ")
	public void insert(TIndicator indicator);
	
	@SQL("update t_indicator set indicatorName=:1.indicatorName,moduleId=:1.moduleId," +
			"indicatorType=:1.indicatorType,tableName=:1.tableName,sampleRate=:1.sampleRate," +
			"filterCond=:1.filterCond,errorCond=:1.errorCond,statTime=:1.statTime,errorType=:1.errorType," +
			"warnOperType=:1.warnOperType,alertOperType=:1.alertOperType,warnValue=:1.warnValue," +
			"alertValue=:1.alertValue,alertEnabled=:1.alertEnabled,enabled=:1.enabled," +
			"urlMethod=:1.urlMethod,urlCharset=:1.urlCharset,urlPage=:1.urlPage,urlHeader=:1.urlHeader," +
			"urlPostData=:1.urlPostData,urlTimeout=:1.urlTimeout,minAlertValue=:1.minAlertValue " +
			"where indicatorId=:1.indicatorId")
	public void update(TIndicator indicator);
	
	@SQL("delete from t_indicator where indicatorId=:1")
	public void delete(int indicatorId);
	
	@SQL("select count(1)>0 from information_schema.tables where table_name = :1")
	public boolean tableExists(String tableName);
	
	@SQL("SELECT indi.indicatorId,indi.indicatorName,indi.moduleId,indi.errorType, "+
		"	st.currentValue,st.currentStatus,st.lastStatus,st.lastProcessTime,st.lastStatusTime,indi.warnValue,indi.alertValue "+
        "FROM t_indicator indi LEFT JOIN t_indicator_status st ON indi.indicatorId = st.indicatorId "+
        "WHERE indi.moduleId = :1 order by indi.indicatorName ")
	public List<IndicatorStatusData> getStatusByModuleId(int moduleId);

	@SQL("select * from t_indicator_status where indicatorId=:1")
	public TIndicatorStatus getStatusById(int indicatorId);
	
	@SQL("insert into t_indicator_process_log(indicatorId,userId,indicatorStatus,createTime,logText,processedTime,processedFlag,lastErrorLog) values (:1.indicatorId,:1.userId,:1.indicatorStatus,:1.createTime,:1.logText,:1.processedTime,:1.processedFlag,:1.lastErrorLog) ")
	public void insertProcessLog(TIndicatorProcessLog processLog);
	
	@SQL("update t_indicator_status set lastStatus=0,lastProcessTime=:2,lastErrorLog=null where indicatorId=:1")
	public void updateStatusForProcess(int indicatorId,Timestamp processTime);
	
	@SQL("select indicatorId,indicatorValue,createTime,indicatorLevel,normalCount,errorCount from ##(:4) where createTime>=:1 and createTime<=:2 and indicatorId=:3 order by createTime ")
	public List<IndicatorData> getIndicatorData(Timestamp start,Timestamp end,int indicatorId,String tableName);
	
	@SQL("update t_indicator_process_log set processedFlag=1,logText=:1.logText where indicatorId=:1.indicatorId and processedFlag=0 ")
	public void updateRecentProcessLog(TIndicatorProcessLog processLog);
	//返回异常恢复的时间
	@SQL("SELECT min(createTime) FROM ##(:4) WHERE `indicatorId` = :1 AND `indicatorLevel` = '0' AND `createTime` >=:2 AND `createTime` <=:3")
	public Timestamp getLastNormalTime(int indicatorId,Timestamp start,Timestamp end,String tableName);
	
	//查询t_url_test_data中的数据
	@SQL("SELECT * FROM ##(:1) WHERE `indicatorId` = :2 and ##(:3) limit :4,:5")
	public List<TUrlTestData> getUrlTest(String tableName,int indicatorId,String queryCond,int start,int limit);
}
