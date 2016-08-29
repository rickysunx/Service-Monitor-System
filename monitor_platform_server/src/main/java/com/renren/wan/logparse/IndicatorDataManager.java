package com.renren.wan.logparse;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.renren.wan.logparse.db.IndicatorDAO;
import com.renren.wan.logparse.db.IndicatorDataDAO;
import com.renren.wan.monitor.data.IndicatorData;
import com.renren.wan.monitor.entities.TIndicator;
import com.renren.wan.monitor.entities.TIndicatorStatus;

public class IndicatorDataManager implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(IndicatorDataManager.class);
	private static IndicatorDataManager instance = null;
	private List<IndicatorData> dataBuffer;
	private Map<Integer,TIndicatorStatus> statusBuffer;
	private long lastFlushTime;
	
	private IndicatorDataManager() {
		dataBuffer = new LinkedList<IndicatorData>();
		statusBuffer = new HashMap<Integer, TIndicatorStatus>();
		lastFlushTime = System.currentTimeMillis();
	}
	
	public static IndicatorDataManager getInstance() {
		if(instance==null) {
			synchronized (IndicatorDataManager.class) {
				if(instance==null) {
					instance = new IndicatorDataManager();
					new Thread(instance,"IndicatorStoreThread").start();
				}
			}
		}
		return instance;
	}
	
	public void saveIndicatorData(IndicatorData data) throws Exception {
		synchronized (this) {
			dataBuffer.add(data);
			
			TIndicatorStatus status = new TIndicatorStatus();
			status.setIndicatorId(data.getIndicatorId());
			status.setCurrentStatus(data.getIndicatorLevel());
			status.setLastStatus(data.getIndicatorLevel());
			
			TIndicator indicator = IndicatorManager.getInstance().getIndicator(data.getIndicatorId());
			status.setCurrentValue(indicator.getErrorType()==1?data.getErrorCount():data.getErrorRate());
			status.setLastErrorLog(GlobalData.getLastErrorLogString(data.getIndicatorId()));
			
			statusBuffer.put(data.getIndicatorId(), status);
		}
	}

	@Override
	public void run() {
		//每秒写入一次数据库
		for(;;) {
			long now = System.currentTimeMillis();
			if(now-lastFlushTime>1000) {
				lastFlushTime = now;
				try {
					List<IndicatorData> oldDataBuffer = null;
					synchronized (this) {
						if(dataBuffer.size()>0) {
							oldDataBuffer = dataBuffer;
							dataBuffer = new LinkedList<IndicatorData>();
						}
					}
					if(oldDataBuffer!=null) {
						new IndicatorDataDAO().save(oldDataBuffer);
					}
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
				
				try {
					Map<Integer,TIndicatorStatus> oldStatusBuffer = null;
					synchronized (this) {
						if(!statusBuffer.isEmpty()) {
							oldStatusBuffer = statusBuffer;
							statusBuffer = new HashMap<Integer, TIndicatorStatus>();
						}
					}
					if(oldStatusBuffer!=null) {
						IndicatorDAO indicatorDAO = new IndicatorDAO();
						Collection<TIndicatorStatus> values = oldStatusBuffer.values();
						List<TIndicatorStatus> statusList = new LinkedList<TIndicatorStatus>(values);
						//更新指标状态
						indicatorDAO.updateIndicatorStatus(statusList);
						//更新指标内存状态
						for(TIndicatorStatus status:statusList) {
							GlobalData.changeIndicatorStatus(status.getIndicatorId(), status.getCurrentStatus());
						}
					}
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
			}
			try {Thread.sleep(100);} catch (Exception e) {}
		}
	}
	
}
