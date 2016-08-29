package com.renren.wan.logparse;

import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.renren.wan.monitor.common.MonitorConstants;
import com.renren.wan.monitor.common.MonitorUtil;
import com.renren.wan.monitor.entities.TIndicator;

public class UrlTestHandler extends Thread {
	
	private Logger logger = LoggerFactory.getLogger(UrlTestHandler.class);
	private int indicatorId;
	
	
	public UrlTestHandler(int indicatorId) {
		super("UrlTestHandlerThread["+indicatorId+"]");
		this.indicatorId = indicatorId;
	}
	
	
	@Override
	public void run() {
		long t_lastrun = 0;
		
		while(true) {
			try {Thread.sleep(100);} catch (Exception e) {}
			
			Map<String, Object> resultMap = null;
			
			try {
				if(!UrlTestManager.getInstance().containsHandler(indicatorId)) break; //如果不被纳入Map管理了，则退出
				TIndicator indicator = IndicatorManager.getInstance().getIndicator(indicatorId);
				if(indicator==null) break; //指标被删除了，不需要再进行检测。
				if(indicator.getIndicatorType()!=MonitorConstants.INDICATOR_TYPE_URL_TEST) break; //非主动监测指标，退出检测。
				
				long now = System.currentTimeMillis();
				if((now-t_lastrun)>=indicator.getSampleRate().longValue()*1000L) {
					t_lastrun = now;
				} else {
					continue;
				}
				
				logger.debug("url开始检测："+indicator.getUrlPage());
				
				resultMap = MonitorUtil.getUrltestResult(indicator);
				
				try {
					LogDispatcher.getInstance().addUrlTest(resultMap);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				
				try {
					UrlTestDataStoreManager.getInstance().addUrlTestData(resultMap);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
				
			
//			JSONObject json = JSONObject.fromObject(resultMap);
//			json.remove("content");
//			logger.info(json.toString());
			
		}
		
	}
	
	
}
