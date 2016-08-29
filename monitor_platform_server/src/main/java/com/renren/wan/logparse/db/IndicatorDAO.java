package com.renren.wan.logparse.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.io.IOUtils;

import com.renren.wan.monitor.data.AlertData;
import com.renren.wan.monitor.data.IndicatorStatusData;
import com.renren.wan.monitor.entities.TIndicator;
import com.renren.wan.monitor.entities.TIndicatorProcessLog;
import com.renren.wan.monitor.entities.TIndicatorStatus;

public class IndicatorDAO {
	
	private static long TS_MAX = 3600*24*1000*36500;
	
	public long queryIndicatorLastUpdate() throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try {
			conn = DataManager.getInstance().getConnection();
			stmt = conn.prepareStatement("select max(ts),count(1) from t_indicator order by indicatorId");
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				Timestamp ts = rs.getTimestamp(1);
				long count = rs.getLong(2);
				if(ts!=null) {
					return ts.getTime()+count*TS_MAX;
				}
			}
		} finally {
			try {stmt.close();} catch (Exception e) {}
			try {conn.close();} catch (Exception e) {}
		}
		return 0;
	}
	
	
	
	public List<TIndicator> queryAll() throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try {
			conn = DataManager.getInstance().getConnection();
			stmt = conn.prepareStatement("select * from t_indicator order by indicatorId");
			ResultSet rs = stmt.executeQuery();
			List<TIndicator> list = new ArrayList<TIndicator>();
			while(rs.next()) {
				TIndicator indi = new TIndicator();
				indi.setIndicatorId(rs.getInt("indicatorId"));
				indi.setIndicatorName(rs.getString("indicatorName"));
				indi.setModuleId(rs.getInt("moduleId"));
				indi.setIndicatorType(rs.getInt("indicatorType"));
				indi.setTableName(rs.getString("tableName"));
				indi.setSampleRate(rs.getInt("sampleRate"));
				indi.setFilterCond(rs.getString("filterCond"));
				indi.setErrorCond(rs.getString("errorCond"));
				indi.setStatTime(rs.getInt("statTime"));
				indi.setErrorType(rs.getInt("errorType"));
				indi.setWarnOperType(rs.getString("warnOperType"));
				indi.setAlertOperType(rs.getString("alertOperType"));
				indi.setWarnValue(rs.getInt("warnValue"));
				indi.setAlertValue(rs.getInt("alertValue"));
				indi.setAlertEnabled(rs.getInt("alertEnabled"));
				indi.setEnabled(rs.getInt("enabled"));
				indi.setUrlCharset(rs.getString("urlCharset"));
				indi.setUrlHeader(rs.getString("urlHeader"));
				indi.setUrlMethod(rs.getString("urlMethod"));
				indi.setUrlPage(rs.getString("urlPage"));
				indi.setUrlPostData(rs.getString("urlPostData"));
				indi.setUrlTimeout(rs.getInt("urlTimeout"));
				indi.setMinAlertValue(rs.getInt("minAlertValue"));
				list.add(indi);
			}
			
			return list;
		} finally {
			try {stmt.close();} catch (Exception e) {}
			try {conn.close();} catch (Exception e) {}
		}
	}
	
	public List<TIndicatorStatus> queryAllStatus() throws Exception {
		QueryRunner qr = new QueryRunner(DataManager.getInstance().getDataSource());
		String sql = "select * from t_indicator_status";
		List<TIndicatorStatus> result = qr.query(sql, new BeanListHandler<TIndicatorStatus>(TIndicatorStatus.class));
		return result;
	}
	
	public TIndicatorStatus getStatusById(int id) throws Exception {
		QueryRunner qr = new QueryRunner(DataManager.getInstance().getDataSource());
		String sql = "select * from t_indicator_status where indicatorId=?";
		TIndicatorStatus result = qr.query(sql, new BeanHandler<TIndicatorStatus>(TIndicatorStatus.class),id);
		return result;
	}
	
	public void insertProcessLog(TIndicatorProcessLog log) throws Exception {
		QueryRunner qr = new QueryRunner(DataManager.getInstance().getDataSource());
		String sql = "insert into t_indicator_process_log " +
				"(indicatorId,userId,indicatorStatus,createTime,processedTime,processedFlag,logText,lastErrorLog) " +
				"values (?,?,?,?,?,?,?,?)";
		qr.update(sql, log.getIndicatorId(),log.getUserId(),log.getIndicatorStatus(),log.getCreateTime(),
				log.getProcessedTime(),log.getProcessedFlag(),log.getLogText(),log.getLastErrorLog());
	}
	
	public void updateIndicatorStatusTime(int indicatorId) throws Exception {
		QueryRunner qr = new QueryRunner(DataManager.getInstance().getDataSource());
		qr.update("update t_indicator_status set lastStatusTime=CURRENT_TIMESTAMP where indicatorId="+indicatorId);
	}
	
	/**
	 * 更新currentStatus和lastStatus
	 * @param indicatorList
	 * @throws Exception
	 */
	public void updateIndicatorStatus(List<TIndicatorStatus> statusList) throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try {
			conn = DataManager.getInstance().getConnection();
			String sql = "update t_indicator_status set lastErrorLog = if(lastStatus>=?,lastErrorLog,?)," +
				"lastStatusTime = if(lastStatus<?,CURRENT_TIMESTAMP,lastStatusTime)," +
				"lastStatus = if(lastStatus>?,lastStatus,?),"+
				"currentStatus=?,currentValue=? where indicatorId = ?";
			stmt = conn.prepareStatement(sql);
			
			for(TIndicatorStatus status:statusList) {
				stmt.setInt(1, status.getLastStatus());
				stmt.setString(2, status.getLastErrorLog());
				stmt.setInt(3, status.getLastStatus());
				stmt.setInt(4, status.getLastStatus());
				stmt.setInt(5, status.getLastStatus());
				stmt.setInt(6, status.getCurrentStatus());
				stmt.setInt(7, status.getCurrentValue());
				stmt.setInt(8, status.getIndicatorId());
				stmt.addBatch();
			}
			stmt.executeBatch();
			
		} finally {
			try {stmt.close();} catch (Exception e) {}
			try {conn.close();} catch (Exception e) {}
		}
	}
	
	public void updateStatusForAlert(List<AlertData> alertDataList) throws Exception {
		if(alertDataList==null || alertDataList.size()==0) return;
		
		BatchUpdater bu = new BatchUpdater();
		
		for(AlertData ad:alertDataList) {
			if(ad.getCurrentStatus()==2) {
				String sql = "update t_indicator_status set errorAlertCount=errorAlertCount+1,normalAlertCount=0 where indicatorId="+ad.getIndicatorId();
				bu.add(sql);
			} else {
				String sql = "update t_indicator_status set errorAlertCount=0,normalAlertCount=normalAlertCount+1 where indicatorId="+ad.getIndicatorId();
				bu.add(sql);
			}
		}
		bu.execute();
		
	}
	
	/**
	 * 更新最后报警时间
	 * @throws Exception
	 */
	public void updateLastAlertTime(List<AlertData> alertDataList) throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		if(alertDataList==null || alertDataList.size()==0) return;
		try {
			conn = DataManager.getInstance().getConnection();
			String sql = "update t_indicator_status set lastAlertTime=CURRENT_TIMESTAMP where indicatorId in (";
			StringBuffer sb = new StringBuffer();
			for(AlertData ad:alertDataList) {
				if(sb.length()>0) {
					sb.append(",");
				}
				sb.append(ad.getIndicatorId());
			}
			sql += sb.toString()+") ";
			stmt = conn.prepareStatement(sql);
			stmt.executeUpdate();
			
		} finally {
			try {stmt.close();} catch (Exception e) {}
			try {conn.close();} catch (Exception e) {}
		}
	}
	
	/**
	 * 查询待发送的警报信息
	 * @return
	 * @throws Exception
	 */
	public List<AlertData> getAlertData() throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		List<AlertData> result = new ArrayList<AlertData>();
		try {
			conn = DataManager.getInstance().getConnection();
			String sql = "SELECT indi.indicatorId,indi.indicatorName,m.moduleName," +
				"m.smsGroup,m.smsType,st.lastStatusTime,st.currentValue,st.currentStatus,st.lastStatus " +
				"FROM t_indicator indi,t_indicator_status st,t_module m " +
				"WHERE indi.indicatorId = st.indicatorId AND indi.moduleId = m.moduleId " +
				"AND m.alertEnabled = 1 AND indi.alertEnabled = 1 " +
				"AND st.lastAlertTime < TIMESTAMPADD(MINUTE,-1*m.alertTime,CURRENT_TIMESTAMP)"+
				"AND st.lastStatus=2 AND "+
				"((st.currentStatus=2 AND st.errorAlertCount<2) OR "+
				"(st.currentStatus<2 AND st.normalAlertCount<1)) ";
			stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				AlertData ad = new AlertData();
				ad.setIndicatorId(rs.getInt("indicatorId"));
				ad.setIndicatorName(rs.getString("indicatorName"));
				ad.setModuleName(rs.getString("moduleName"));
				ad.setSmsGroup(rs.getString("smsGroup"));
				ad.setSmsType(rs.getString("smsType"));
				ad.setLastStatusTime(rs.getTimestamp("lastStatusTime"));
				ad.setCurrentValue(rs.getInt("currentValue"));
				ad.setCurrentStatus(rs.getInt("currentStatus"));
				ad.setLastStatus(rs.getInt("lastStatus"));
				result.add(ad);
			}
		} finally {
			try {stmt.close();} catch (Exception e) {}
			try {conn.close();} catch (Exception e) {}
		}
		return result;
	}
	
}
