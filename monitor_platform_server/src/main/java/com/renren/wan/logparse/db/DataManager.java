package com.renren.wan.logparse.db;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Properties;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.DataSources;

public class DataManager {

	private static DataManager instance = null;
	private static final String DB_PROP_FILE = "conf/c3p0.cfg";
	private DataSource dataSource = null;
	
	private DataManager() {
		try {
			Properties prop = new Properties();
			prop.load(new FileInputStream(DB_PROP_FILE));
			
			String jdbcDriver = prop.getProperty("jdbcDriver");
			String jdbcUrl = prop.getProperty("jdbcUrl");
			String username = prop.getProperty("username");
			String password = prop.getProperty("password");
			
			Class.forName(jdbcDriver);
			
			Properties c3p0_props = (Properties) prop.clone();
			DataSource ds_unpooled = DataSources.unpooledDataSource(jdbcUrl, username, password);
			DataSource ds_pooled = DataSources.pooledDataSource(ds_unpooled,c3p0_props);
			dataSource = ds_pooled;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static DataManager getInstance() {
		if(instance==null) {
			synchronized (DatabaseMetaData.class) {
				if(instance==null) {
					instance = new DataManager();
				}
			}
		}
		return instance;
	}
	
	public DataSource getDataSource() throws Exception {
		return dataSource;
	}
	
	public Connection getConnection() throws Exception {
		return dataSource.getConnection();
	}
}
