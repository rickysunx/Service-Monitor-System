package com.renren.wan.logparse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.renren.wan.logparse.db.IndicatorDAO;
import com.renren.wan.monitor.entities.TIndicator;

public class IndicatorManager implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(IndicatorManager.class);
	private static IndicatorManager instance = null;
	
	private List<TIndicator> indicatorList;
	private Map<Integer,TIndicator> indicatorMap;
	private long lastUpdate;     //单位：毫秒
	private IndicatorDAO indicatorDAO;
	
	private IndicatorManager() {
		try {
			logger.debug("初始化IndicatorManager");
			indicatorDAO = new IndicatorDAO();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void reloadIndicator() throws Exception {
		List<TIndicator> list = indicatorDAO.queryAll();
		logger.info("重新加载"+list.size()+"条指标数据");
		Map<Integer,TIndicator> map = new HashMap<Integer, TIndicator>();
		for(TIndicator indi:list) {
			map.put(indi.getIndicatorId(), indi);
		}
		
		synchronized (this) {
			indicatorList = list;
			indicatorMap = map;
		}
	}
	
	public void queryLastUpdate() throws Exception {
		lastUpdate = indicatorDAO.queryIndicatorLastUpdate();
	}
	
	public static IndicatorManager getInstance() throws Exception {
		if(instance==null) {
			synchronized (IndicatorManager.class) {
				if(instance==null) {
					instance = new IndicatorManager();
					instance.reloadIndicator();
					instance.queryLastUpdate();
					new Thread(instance,"IndicatorReloadCheckThread").start();
				}
			}
		}
		return instance;
	}
	
	public TIndicator getIndicator(int indicatorId) {
		Map<Integer,TIndicator> map = getIndicatorMap();
		return map.get(indicatorId);
	}
	
	public Map<Integer,TIndicator> getIndicatorMap() {
		synchronized (this) {
			return indicatorMap;
		}
	}
	
	public List<TIndicator> getIndicatorList() {
		synchronized (this) {
			return indicatorList;
		}
	}

	/**
	 * 监控指标变化线程
	 */
	@Override
	public void run() {
		for(;;) {
			try {
				long newLastUpdate = indicatorDAO.queryIndicatorLastUpdate();
				if(newLastUpdate!=lastUpdate) {
					lastUpdate = newLastUpdate;
					reloadIndicator();
				}
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
			
			try {Thread.sleep(3000);} catch (Exception e) {}
		}
	}
}
