package com.renren.wan.logparse;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.renren.wan.monitor.common.MonitorConstants;
import com.renren.wan.monitor.entities.TIndicator;

public class UrlTestManager implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(IndicatorManager.class);
	private static UrlTestManager instance = null;
	private Map<Integer,UrlTestHandler> handlerMap = Collections.synchronizedMap(new HashMap<Integer, UrlTestHandler>());
	
	
	protected UrlTestManager() {
		
	}
	
	protected void init() throws Exception {
		new Thread(this,"UrlTestManagerThread").start();
	}
	
	public static UrlTestManager getInstance() throws Exception {
		if(instance==null) {
			synchronized (UrlTestManager.class) {
				if(instance==null) {
					instance = new UrlTestManager();
					instance.init();
				}
			}
		}
		return instance;
	}
	
	public boolean containsHandler(int indicatorId) {
		return handlerMap.containsKey(indicatorId);
	}
	
	

	@Override
	public void run() {
		while(true) {
			try {Thread.sleep(1000);} catch (Exception e) {}
			
			try {
				List<TIndicator> indicatorList = IndicatorManager.getInstance().getIndicatorList();
				
				//删掉不再运行的线程
				Set<Integer> keySet = handlerMap.keySet();
				for(int indicatorId:keySet) {
					UrlTestHandler handler = handlerMap.get(indicatorId);
					if(handler!=null) {
						if(!handler.isAlive()) {
							handlerMap.remove(handler);
						}
					}
				}
				
				for(TIndicator indicator:indicatorList) {
					if(indicator.getIndicatorType()==MonitorConstants.INDICATOR_TYPE_URL_TEST) {
						int indicatorId = indicator.getIndicatorId();
						if(!handlerMap.containsKey(indicator.getIndicatorId())) {
							UrlTestHandler handler = new UrlTestHandler(indicatorId);
							handlerMap.put(indicatorId, handler);
							handler.start();
						}
					}
				}
				
				
				
			} catch (Throwable e) {
				logger.error(e.getMessage(), e);
			}
			
			
		}
	}
}
