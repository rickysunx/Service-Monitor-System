package com.renren.wan.logparse;

import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.renren.wan.logparse.db.IndicatorDAO;
import com.renren.wan.monitor.common.MonitorUtil;
import com.renren.wan.monitor.data.AlertData;


public class AlertManager implements Runnable {
	
	private static Logger logger = LoggerFactory.getLogger(AlertManager.class);
	private static AlertManager instance = null;
	
	private IndicatorDAO indicatorDAO;
	
	private AlertManager() {
		logger.debug("初始化AlertManager");
		indicatorDAO = new IndicatorDAO();
	}
	
	public static AlertManager getInstance() {
		if(instance==null) {
			synchronized (AlertManager.class) {
				if(instance==null) {
					instance = new AlertManager();
					new Thread(instance,"AlertManagerThread").start();
				}
			}
		}
		return instance;
	}
	
	
	/**
	 * 
	 */
	public void sendSms(String msg,String type,String group) throws Exception {
		
		logger.info("发送短信："+msg);
		
		String disbledalert = System.getProperty("monitor.disablealert");
		if(disbledalert!=null && disbledalert.equalsIgnoreCase("true")) {
			return;
		}
		
		String title = "监控系统报警";
		String encoding = "GBK";
		String mt = URLEncoder.encode(title,encoding);
		String c = URLEncoder.encode(msg, encoding);
		long a = System.currentTimeMillis()/1000;
		String s = type;
		String m = group;
		String url = "http://warn.io8.org/api/?c="+c+"&a="+a+"&s="+s+"&m="+m+"&mt="+mt;
		String response = MonitorUtil.sendRequest(url);
		if(!response.equals("msg:ok")) {
			throw new Exception("短信平台返回错误："+response);
		}
	}
	

	/**
	 * 报警线程
	 */
	@Override
	public void run() {
		for(;;) {
			try {
				List<AlertData> alertDataList = indicatorDAO.getAlertData();
				indicatorDAO.updateLastAlertTime(alertDataList);
				indicatorDAO.updateStatusForAlert(alertDataList);
				for(AlertData ad:alertDataList) {
					try {
						String msg = "平台监控:模块["+ad.getModuleName()+"]指标["+ad.getIndicatorName()+"]" +
							(ad.getCurrentStatus()==2?"出错":"恢复")+",当前值:"+ad.getCurrentValue()+",时间:"+getShortTime(ad.getLastStatusTime())+"";
						sendSms(msg, ad.getSmsType(), ad.getSmsGroup());
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
			
			try {Thread.sleep(5000);} catch (Exception e) {}
		}
	}
	
	protected String getShortTime(Timestamp ts) {
		DateFormat df = new SimpleDateFormat("MM/dd HH:mm",Locale.ENGLISH);
		df.setTimeZone(TimeZone.getDefault());
		return df.format(ts.getTime());
	}
	
}
