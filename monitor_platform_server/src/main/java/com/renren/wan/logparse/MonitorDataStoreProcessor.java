package com.renren.wan.logparse;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.renren.wan.logparse.db.MonitorDataDAO;
import com.renren.wan.monitor.data.MonitorData;
import com.renren.wan.monitor.entities.TIndicator;

public class MonitorDataStoreProcessor implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(MonitorDataStoreProcessor.class);
	public long timestamp;
	
	public MonitorDataStoreProcessor(long ts) {
		this.timestamp = ts;
	}
	
	@Override
	public void run() {
		try {
			
			List<TIndicator> indicatorList = IndicatorManager.getInstance().getIndicatorList();
			MonitorDataManager mdm = MonitorDataManager.getInstance();
			List<MonitorData> dataList = new ArrayList<MonitorData>();
			for(TIndicator indi:indicatorList) {
				MonitorData md = mdm.getMonitorData(indi.getIndicatorId(), timestamp);
				if(md!=null) {
					dataList.add(md);
				}
			}
			if(dataList.size()>0) {
				logger.debug("写入监控数据，ts:"+timestamp+" count:"+dataList.size());
				new MonitorDataDAO().save(dataList);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
