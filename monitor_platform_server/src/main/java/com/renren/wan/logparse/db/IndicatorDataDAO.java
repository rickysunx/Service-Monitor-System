package com.renren.wan.logparse.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import com.google.gson.JsonObject;
import com.renren.wan.monitor.common.MonitorUtil;
import com.renren.wan.monitor.data.IndicatorData;

public class IndicatorDataDAO {
	
	
	public void saveUrlTestData(List<Map<String,Object>> dataList) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		
		try {
			if(dataList.size()==0) return;
			
			conn = DataManager.getInstance().getConnection();
			stmt = conn.createStatement();
			
			//找出需要创建的表
			Set<String> tableNameSet = new HashSet<String>();
			for(Map<String,Object> data:dataList) {
				long createTime = (Long)data.get("createTime");
				String tableName = "t_url_test_data_"+MonitorUtil.ts2date(createTime);
				data.put("tableName", tableName);
				tableNameSet.add(tableName);
			}
			
			for(String tableName:tableNameSet) {
				String sqlCreate = "create table if not exists "+tableName+" like t_url_test_data ";
				stmt.addBatch(sqlCreate);
			}
			stmt.executeBatch();
			stmt.close();
			stmt = null;
			
			for(String tableName:tableNameSet) {
				PreparedStatement pstmt = null;
				try {
					pstmt = conn.prepareStatement("insert into "+tableName+
						" (indicatorId,success,statusCode,spendTime,header,content,createTime) values " +
						" (?,?,?,?,?,?,?) ");
					
					for(Map<String,Object> data:dataList) {
						String tableName0 = (String)data.get("tableName");
						if(tableName.equals(tableName0)) {
							@SuppressWarnings("unchecked")
							Map<String,String> headerMap = (Map<String,String>)data.get("headers") ;
							String header = (headerMap==null?null:JSONObject.fromObject(headerMap).toString());
							if(header!=null && header.length()>4096) {
								header = header.substring(0,4096);
							}
							
							String content = (String)data.get("content");
							if(content!=null && content.length()>4096) {
								content = content.substring(0,4096);
							}
							
							pstmt.setObject(1, (Integer)data.get("indicatorId"));
							pstmt.setObject(2, ((Boolean)data.get("success"))?1:0);
							pstmt.setObject(3, (Integer)data.get("statusCode"));
							pstmt.setObject(4, (Integer)data.get("spendTime"));
							pstmt.setString(5, header);
							pstmt.setString(6, content);
							pstmt.setTimestamp(7, new Timestamp((Long)data.get("createTime")));
							pstmt.addBatch();
						}
						
					}
					pstmt.executeBatch();
					
				} finally {
					try {if(pstmt!=null) pstmt.close();} catch (Exception e) {}
				}
				
			}
			
			
//			for(Map<String,Object> data:dataList) {
//				String sql = "insert into " + data.get("tableName") +
//					"(indicatorId,normalCount,errorCount,createTime,indicatorLevel,indicatorValue) values ( " +
//					data.getIndicatorId()+","+
//					data.getNormalCount()+","+
//					data.getErrorCount()+","+
//					"timestamp('"+data.getCreateTime()+"'),"+
//					data.getIndicatorLevel()+","+
//					data.getIndicatorValue()+")";
//				stmt.addBatch(sql);
//			}
//			stmt.executeBatch();
			
		} finally {
			try {if(stmt!=null) stmt.close();} catch (Exception e) {}
			try {if(conn!=null) conn.close();} catch (Exception e) {}
		}
	}
	
	/**
	 * 保存指标数据
	 * @param data
	 * @throws Exception
	 */
	public void save(List<IndicatorData> dataList) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		
		try {
			if(dataList.size()==0) return;
			
			conn = DataManager.getInstance().getConnection();
			stmt = conn.createStatement();
			
			//找出需要创建的表
			Set<String> tableNameSet = new HashSet<String>();
			for(IndicatorData data:dataList) {
				tableNameSet.add(data._getTableName());
			}
			
			for(String tableName:tableNameSet) {
				String sqlCreate = "create table if not exists "+tableName+" like t_indicator_data ";
				stmt.addBatch(sqlCreate);
			}
			
			for(IndicatorData data:dataList) {
				String sql = "insert into " + data._getTableName() +
					"(indicatorId,normalCount,errorCount,createTime,indicatorLevel,indicatorValue) values ( " +
					data.getIndicatorId()+","+
					data.getNormalCount()+","+
					data.getErrorCount()+","+
					"timestamp('"+data.getCreateTime()+"'),"+
					data.getIndicatorLevel()+","+
					data.getIndicatorValue()+")";
				stmt.addBatch(sql);
			}
			stmt.executeBatch();
			
		} finally {
			try {stmt.close();} catch (Exception e) {}
			try {conn.close();} catch (Exception e) {}
		}
	}
}
