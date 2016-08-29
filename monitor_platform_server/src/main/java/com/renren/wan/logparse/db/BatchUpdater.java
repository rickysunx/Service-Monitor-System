package com.renren.wan.logparse.db;

import java.sql.Connection;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;


public class BatchUpdater {
	
	private List<String> sqlList = new LinkedList<String>(); 
	
	public BatchUpdater() {
		
	}
	
	public void add(String sql) {
		sqlList.add(sql);
	}
	
	public void execute() throws Exception {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = DataManager.getInstance().getConnection();
			stmt = conn.createStatement();
			
			for(String sql:sqlList) {
				stmt.addBatch(sql);
			}
			stmt.executeBatch();
			
		} finally {
			try {stmt.close();} catch (Exception e) {}
			try {conn.close();} catch (Exception e) {}
		}
	}
}
