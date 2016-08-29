package com.renren.wan.logparse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.renren.wan.logparse.db.IndicatorDataDAO;

public class UrlTestDataStoreManager implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(IndicatorManager.class);
	private static UrlTestDataStoreManager instance = null;
	private List<Map<String,Object>> urlTestDataList = new ArrayList<Map<String,Object>>();
	
	protected UrlTestDataStoreManager() {
		
	}
	
	protected void init() {
		new Thread(this,"UrlTestDataStoreThread").start();
	}
	
	public static UrlTestDataStoreManager getInstance() throws Exception {
		if(instance==null) {
			synchronized (UrlTestDataStoreManager.class) {
				if(instance==null) {
					instance = new UrlTestDataStoreManager();
					instance.init();
				}
			}
		}
		return instance;
	}
	
	public void addUrlTestData(Map<String, Object> data) {
		synchronized (this) {
			urlTestDataList.add(data);
		}
	}

	public void saveUrlTestData() throws Exception {
		List<Map<String,Object>> oldUrlTestData = null;
		synchronized (this) {
			if(urlTestDataList.size()>0) {
				oldUrlTestData = urlTestDataList;
				urlTestDataList = new ArrayList<Map<String,Object>>();
			}
		}
		
		if(oldUrlTestData!=null) {
			IndicatorDataDAO dao = new IndicatorDataDAO();
			dao.saveUrlTestData(oldUrlTestData);
		}
		
	}

	@Override
	public void run() {
		while(true) {
			try {Thread.sleep(1000);} catch (Exception e) { }
			
			try {
				saveUrlTestData();
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
		
	}
	
}
