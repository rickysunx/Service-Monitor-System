package com.renren.wan.logparse;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scribe.thrift.LogEntry;

public class LogDispatcher implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(LogDispatcher.class);
	private static LogDispatcher instance = null;
	private ExecutorService execLog = null;
	private ExecutorService execIndicator = null;
	private ExecutorService execLogStore = null;
	private AtomicLong currentTimeStamp = new AtomicLong();  //in seconds
	private AtomicLong workingTimeStamp = new AtomicLong();  //in seconds
	private Map<Long, Long> taskCountMap = new Hashtable<Long, Long>();
	private long lastOverloadLogTime = 0;
	private long lastOverloadAlertTime = 0;
	
	private LogDispatcher() {
		execLog = Executors.newFixedThreadPool(
				Runtime.getRuntime().availableProcessors(),
				new LogParseThreadFactory("log"));
		execIndicator = Executors.newFixedThreadPool(
				Math.max(1,Runtime.getRuntime().availableProcessors()/2),
				new LogParseThreadFactory("indi"));
		execLogStore = Executors.newFixedThreadPool(
				Math.max(1,Runtime.getRuntime().availableProcessors()/2),
				new LogParseThreadFactory("logstore"));
		long now = System.currentTimeMillis()/1000;
		currentTimeStamp.set(now);
		workingTimeStamp.set(now);
		
	}
	
	public static LogDispatcher getInstance() {
		if(instance==null) {
			synchronized (LogDispatcher.class) {
				if(instance==null) {
					instance = new LogDispatcher();
					new Thread(instance,"LogDoneCheckThread").start();
				}
			}
		}
		return instance;
	}
	
	public synchronized void incrementTaskCount(long timestamp) {
		Long count = taskCountMap.get(timestamp);
		if(count==null) {
			taskCountMap.put(timestamp, 1L);
		} else {
			taskCountMap.put(timestamp, count+1);
		}
	}
	
	public synchronized void decrementTaskCount(long timestamp) {
		Long count = taskCountMap.get(timestamp);
		 if(count!=null) {
			 count--;
			 if(count==0) {
				 taskCountMap.remove(timestamp);
			 } else {
				 taskCountMap.put(timestamp, count);
			 }
		 }
	}
	
	public synchronized long getTaskCount(long timestamp) {
		Long count = taskCountMap.get(timestamp);
		//logger.info("count:"+count);
		if(count==null) {
			return 0;
		} else {
			return count;
		}
	}
	
	public void addUrlTest(Map<String,Object> map) throws Exception {
		long now = currentTimeStamp.get();
		map.put("createTime", now*1000L);
		LogProcessor processor = new LogProcessor(map,now,
				IndicatorManager.getInstance().getIndicatorList());
		incrementTaskCount(now);
		execLog.execute(processor);
	}
	
	public void addMessages(List<LogEntry> messages) throws Exception {
		for(LogEntry message:messages) {
			long now = currentTimeStamp.get();
			LogProcessor processor = new LogProcessor(message,now,
					IndicatorManager.getInstance().getIndicatorList());
			incrementTaskCount(now);
			execLog.execute(processor);
		}
	}

	/**
	 * 同步维护线程
	 */
	@Override
	public void run() {
		for(;;) {
			try {
				long now = System.currentTimeMillis()/1000;
				currentTimeStamp.set(now);
				long working = workingTimeStamp.get();
				
				if(now-working>10) {
					if(now-lastOverloadLogTime>1) {
						logger.error("-------系统过载-----");
						lastOverloadLogTime = now;
					}
					if(now-lastOverloadAlertTime>600) {
						try {
							AlertManager.getInstance().sendSms("监控系统过载", "monitor_platform_alert", "monitor_platform_alert");
						} catch (Exception e) {
							logger.error("系统过载发送报警出错",e);
						}
						lastOverloadAlertTime = now;
					}
				}
				
				if(now>working && getTaskCount(working)==0) {
					execIndicator.execute(new IndicatorProcessor(working));
					execLogStore.execute(new MonitorDataStoreProcessor(working));
					workingTimeStamp.incrementAndGet();
				}
				
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
			
			try {
				Thread.sleep(100);
			} catch (Exception e) {}
		}
	}
	
}
