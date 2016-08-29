package com.renren.wan.logparse;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.renren.wan.logparse.db.MonitorDataDAO;
import com.renren.wan.monitor.common.MonitorUtil;
import com.renren.wan.monitor.data.MonitorData;
import com.renren.wan.monitor.data.MonitorDayData;

public class MonitorDataManager {
	private static Logger logger = LoggerFactory.getLogger(MonitorDataManager.class);
	private static MonitorDataManager instance = null;
	private static int dataCacheSize = 1000;  //内存中最大的缓存个数
	
	private Map<Integer,MonitorDayData> dataMap; //key-"indicatorId-date" value-"indexOfDataBuffer"
	private List<MonitorDayData> dataCache; //缓存的监控数据
	
	private int timeZoneOffset;
	private static final int DAYS_TOTAL = 36500;
	
	private MonitorDataManager() {
		timeZoneOffset = Calendar.getInstance().getTimeZone().getRawOffset()/1000;
		dataMap = Collections.synchronizedMap(new TreeMap<Integer,MonitorDayData>());
		dataCache = new ArrayList<MonitorDayData>();
	}
	
	private int getKey(int indicatorId,int days) {
		return indicatorId*DAYS_TOTAL+days;
	}
	
	/**
	 * 
	 * @param timestamp 单位秒
	 * @return
	 */
	private int getDaysFrom1970(long timestamp) {
		return (int)((timestamp+timeZoneOffset)/86400);
	}
	
//	private String days2date(int days) {
//		long ts = ((long)(days*86400-timeZoneOffset))*1000;
//		return MonitorUtil.ts2date(ts);
//	}
//	
//	private void showCacheDays() {
//		Set<String> dateSet = new HashSet<String>();
//		for(MonitorDayData mdd:dataCache) {
//			dateSet.add( days2date(mdd.getDate()));
//		}
//		for(String s:dateSet) {
//			System.out.println(s);
//		}
//	}
	
	public static MonitorDataManager getInstance() throws Exception {
		if(instance==null) {
			synchronized (MonitorDataManager.class) {
				if(instance==null) {
					instance = new MonitorDataManager();
					instance.loadMonitorData();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 从数据库加载监控数据
	 * 
	 */
	public void loadMonitorData() throws Exception {
		logger.info("从数据库加载监控数据");
		new MonitorDataDAO().loadMonitorData();
	}
	
	/**
	 * 获取MonitorDayData
	 * @param indicatorId
	 * @param date
	 * @return
	 * @throws Exception
	 */
	private MonitorDayData getMonitorDayData(int indicatorId,int days) throws Exception {
		int key = getKey(indicatorId, days);
		MonitorDayData mdd = dataMap.get(key);
		return mdd;
	}
	
	private MonitorDayData getMonitorDayDataWithCreate(int indicatorId,int days) throws Exception {
		int key = getKey(indicatorId, days);
		MonitorDayData mdd = dataMap.get(key);
		if(mdd==null) {
			logger.debug("创建新监控Cache");
			mdd = getNewMonitorDayData(indicatorId, days);
			dataMap.put(key, mdd);
		}
		return mdd;
	}
	
	private MonitorDayData getNewMonitorDayData(int indicatorId,int days) {
		MonitorDayData mdd = null;
		if(dataCache.size()<dataCacheSize) {
			mdd = new MonitorDayData();
			dataCache.add(mdd);
		} else {
			mdd = findFreeMonitorDayData();
			if(mdd==null) {
				mdd = freeMonitorDayData();
			}
			if(mdd==null) {
				throw new RuntimeException("从内存池获取空闲监控数据空间失败！");
			}
		}
		mdd.setFree(false);
		mdd.setDate(days);
		mdd.setIndicatorId(indicatorId);
		return mdd;
	}
	
//	public static void main(String [] args) {
//		try {
//			MonitorDataManager mdm = MonitorDataManager.getInstance();
//			Calendar c = Calendar.getInstance();
//			c.set(2012, 3, 1, 0, 0, 0);
//			c.set(Calendar.MILLISECOND, 0);
//			long t = c.getTimeInMillis()/1000;
//			
//			Random r = new Random(System.currentTimeMillis());
//			
//			FileWriter fori = new FileWriter("d:\\data\\ori.txt");
//			
//			for(long time=t;time<t+10*86400;time+=600) {
//				String s = MonitorUtil.ts2datetime(time*1000);
//				if(s.endsWith("00:00")) {
//					System.out.println(s);
//				}
//				if(s.endsWith("00:00:00")) {
//					mdm.showCacheDays();
//				}
//				for(int id=1;id<=80;id++) {
//					int normalCount = r.nextInt(100);
//					int errorCount = r.nextInt(100);
//					String dd = "id:["+id+"] time:["+MonitorUtil.ts2datetime(time*1000)+"] normal:["+normalCount+"] error:["+errorCount+"]\r\n";
//					fori.write(dd);
//					mdm.setMonitorData(id, time, normalCount, errorCount);
////					for(int i=0;i<normalCount;i++) {
////						mdm.addMonitorData(id, time, 1);
////					}
////					for(int i=0;i<errorCount;i++) {
////						mdm.addMonitorData(id, time, 2);
////					}
//				}
//			}
//			fori.flush();
//			fori.close();
//			
//			FileWriter fmap = new FileWriter("d:\\data\\map.txt");
//			for(long time=t;time<=t+15*86400;time+=300) {
//				String s = MonitorUtil.ts2datetime(time*1000);
//				if(s.endsWith("00:00")) {
//					System.out.println(s);
//				}
//				for(int id=1;id<=100;id++) {
//					MonitorData md = mdm.getMonitorData(id, time);
//					if(md!=null) {
//						String dd = "id:["+md.getIndicatorId()+
//							"] time:["+ MonitorUtil.ts2datetime(time*1000)+"] normal:["+
//							md.getNormalCount()+"] error:["+
//							md.getErrorCount()+"]\r\n";
//						fmap.write(dd);
//					}
//				}
//			}
//			fmap.flush();
//			fmap.close();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	private MonitorDayData freeMonitorDayData() {
		MonitorDayData freeMdd = null;
		int minDate = -1;
		int freeCount = Math.max(1, dataCacheSize/50);
		//查找最小日期
		for(MonitorDayData mdd:dataCache) {
			if(!mdd.isFree()) {
				if(minDate==-1) {
					minDate = mdd.getDate();
				} else {
					if(minDate>mdd.getDate()) {
						minDate = mdd.getDate();
					}
				}
			}
		}
		//释放最小日期的内容
		int freeIndex = 0;
		for(MonitorDayData mdd:dataCache) {
			if(freeIndex>=freeCount) {
				break;
			}
			if(!mdd.isFree()) {
				if(mdd.getDate()==minDate) {
					int days = mdd.getDate();
					int indicatorId = mdd.getIndicatorId();
					int key = getKey(indicatorId, days);
					dataMap.remove(key);
					mdd.free();
					freeMdd = mdd;
					freeIndex++;
				}
			}
		}
		logger.info("释放了"+freeIndex+"条监控数据");
		return freeMdd;
	}
	
	private MonitorDayData findFreeMonitorDayData() {
		for(MonitorDayData mdd:dataCache) {
			if(mdd.isFree()) return mdd;
		}
		return null;
	}
	
	public MonitorDayData getMonitorDayDataByTs(int indicatorId,long createTime) throws Exception {
		int days = getDaysFrom1970(createTime);
		MonitorDayData mdd = getMonitorDayData(indicatorId, days);
		return mdd;
	}
	
	public MonitorData getMonitorData(int indicatorId,long createTime) throws Exception {
		long ts = createTime*1000;
		int days = getDaysFrom1970(createTime);
		MonitorDayData mdd = getMonitorDayData(indicatorId, days);
		if(mdd!=null) {
			MonitorData md = new MonitorData();
			md.setCreateTime(createTime);
			md.setErrorCount(mdd.getErrorCount(ts));
			md.setNormalCount(mdd.getNormalCount(ts));
			md.setIndicatorId(indicatorId);
			return md;
		}
		return null;
	}
	
	/**
	 * 设置监控数据
	 * @param createTime 单位：秒
	 * @param status 1-正常 2-错误
	 */
	public void setMonitorData(int indicatorId,long createTime,int normalCount,int errorCount) throws Exception {
		long ts = createTime*1000;
		int days = getDaysFrom1970(createTime);
		MonitorDayData mdd = getMonitorDayDataWithCreate(indicatorId, days);
		mdd.setNormalCount(ts, normalCount);
		mdd.setErrorCount(ts, errorCount);
	}
	
	/**
	 * 增加监控数据
	 * @param createTime 单位：秒
	 * @param status 1-正常 2-错误
	 */
	public synchronized void addMonitorData(int indicatorId,long createTime,int status) throws Exception {
		long ts = createTime*1000;
		int days = getDaysFrom1970(createTime);
		MonitorDayData mdd = getMonitorDayDataWithCreate(indicatorId, days);
		if(status==1) {
			mdd.incrementNormalCount(ts);
		} else if(status==2) {
			mdd.incrementErrorCount(ts);
		}
	}
}
