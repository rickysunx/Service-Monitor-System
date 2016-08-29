package com.renren.wan.logparse.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.renren.wan.logparse.MonitorDataManager;
import com.renren.wan.monitor.common.MonitorUtil;
import com.renren.wan.monitor.data.MonitorData;

public class MonitorDataDAO {
	private static Logger logger = LoggerFactory.getLogger(MonitorDataDAO.class);
	
	public void save(List<MonitorData> dataList) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		
		try {
			if(dataList.size()==0) return;
			
			conn = DataManager.getInstance().getConnection();
			stmt = conn.createStatement();
			
			//找出需要创建的表
			Set<String> tableNameSet = new HashSet<String>();
			for(MonitorData data:dataList) {
				tableNameSet.add(data._getTableName());
			}
			
			for(String tableName:tableNameSet) {
				String sqlCreate = "create table if not exists "+tableName+" like t_monitor_data ";
				stmt.addBatch(sqlCreate);
			}
			
			for(MonitorData data:dataList) {
				String sql = "insert into "+ data._getTableName() +
					"(indicatorId,normalCount,errorCount,createTime) values (" +
					data.getIndicatorId()+","+
					data.getNormalCount()+","+
					data.getErrorCount()+","+
					"timestamp('"+new Timestamp(data.getCreateTime()*1000)+"'))";
				
				stmt.addBatch(sql);
			}
			stmt.executeBatch();
			
		} finally {
			try {stmt.close();} catch (Exception e) {}
			try {conn.close();} catch (Exception e) {}
		}
	}
	
//	public static void main(String [] args) {
//		long now = System.currentTimeMillis();
//		long start = MonitorUtil.getDayStartTs(now-(3600*24*1000*3)); //3天前的时间戳
//		long end = MonitorUtil.getDayEndTs(now);
//		
//		Timestamp ts_start = new Timestamp(start);
//		System.out.println(ts_start);
//		
//		Timestamp ts_end = new Timestamp(end);
//		System.out.println(ts_end);
//	}
	
	public void loadMonitorData() throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try {
			
			long now = System.currentTimeMillis();
			long start = MonitorUtil.getDayStartTs(now-(3600*24*1000*3)); //3天前的时间戳
			long end = MonitorUtil.getDayEndTs(now);
			
			conn = DataManager.getInstance().getConnection();
			
			List<String> tableNameList = new ArrayList<String>();
			
			//获取表名
			String sqlTable = "select table_name from information_schema.tables where table_schema=database() " +
				"and table_name >= ? and table_name <= ?";
			stmt = conn.prepareStatement(sqlTable);
			stmt.setString(1, "t_monitor_data_"+MonitorUtil.ts2date(start));
			stmt.setString(2, "t_monitor_data_"+MonitorUtil.ts2date(end));
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				tableNameList.add(rs.getString(1));
			}
			
			int count = 0;
			
			if(tableNameList.size()>0) {
				stmt.close();
				
				StringBuffer sql = new StringBuffer();
				for(String tableName:tableNameList) {
					if(sql.length()>0) {
						sql.append(" union all ");
					}
					sql.append("select indicatorId,normalCount,errorCount,createTime from "+tableName);
				}
				
				stmt = conn.prepareStatement(sql.toString());
				rs = stmt.executeQuery();
				MonitorDataManager mdm = MonitorDataManager.getInstance();
				
				while(rs.next()) {
					count++;
					int indicatorId = rs.getInt("indicatorId");
					int normalCount = rs.getInt("normalCount");
					int errorCount = rs.getInt("errorCount");
					Timestamp createTimeTs = rs.getTimestamp("createTime");
					mdm.setMonitorData(indicatorId, createTimeTs.getTime()/1000, normalCount, errorCount);
				}
			} 
			
			logger.info("加载监控数据成功：共计"+count+"条");
		} finally {
			try {stmt.close();} catch (Exception e) {}
			try {conn.close();} catch (Exception e) {}
		}
	}
}
