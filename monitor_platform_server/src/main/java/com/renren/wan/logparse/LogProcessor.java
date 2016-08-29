package com.renren.wan.logparse;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scribe.thrift.LogEntry;

import com.renren.wan.monitor.common.JsonMapContext;
import com.renren.wan.monitor.common.MonitorConstants;
import com.renren.wan.monitor.common.MonitorUtil;
import com.renren.wan.monitor.entities.TIndicator;

public class LogProcessor implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(LogProcessor.class);
	private LogEntry message;
	private long timestamp;
	private List<TIndicator> indicatorList;
	private int msgType;
	private Map<String, Object> map;
	
	public static final int MSG_TYPE_LOG = 0;
	public static final int MSG_TYPE_URL_TEST = 1;
	
	public LogProcessor(LogEntry message,long timestamp,List<TIndicator> indicatorList) {
		this.message = message;
		this.timestamp = timestamp;
		this.indicatorList = indicatorList;
		this.msgType = MSG_TYPE_LOG;
	}
	
	public LogProcessor(Map<String, Object> map,long timestamp,List<TIndicator> indicatorList) {
		this.map = map;
		this.timestamp = timestamp;
		this.indicatorList = indicatorList;
		this.msgType = MSG_TYPE_URL_TEST;
	}
	
	@Override
	public void run() {
		try {
			if(msgType==MSG_TYPE_LOG) {
				String category = message.category;
				String msg = message.message;
				logger.debug("处理监控数据 cate:"+category+" msg:"+msg);
				if(category.startsWith("log.")) {
					String tableName = category.substring(category.indexOf(".")+1);
					logger.debug("tableName:["+tableName+"]");
					JsonMapContext context = new JsonMapContext(msg);
					for(TIndicator indi:indicatorList) {
						logger.debug("indi_tableName:["+tableName+"]");
						if(indi.getEnabled()!=null 
								&& indi.getIndicatorType().intValue()==MonitorConstants.INDICATOR_TYPE_LOG
								&& indi.getEnabled().intValue()==1 
								&& indi.getTableName()!=null 
								&& indi.getTableName().equalsIgnoreCase(tableName)) {
							logger.debug("开始处理日志："+msg);
							boolean filtered = MonitorUtil.parseCondition(indi.getFilterCond(), context);
							boolean error = false;
							if(filtered) {
								error = MonitorUtil.parseCondition(indi.getErrorCond(), context);
								//设置最后一条错误日志
								if(error) GlobalData.setLastErrorLog(indi.getIndicatorId(), msg);
								MonitorDataManager.getInstance().addMonitorData(indi.getIndicatorId(), timestamp, error?2:1);
							}
							logger.debug("监控数据处理结果：filtered="+filtered+" error="+error+" cate:"+category+" msg:"+msg);
						}
					}
				}
			} else if(msgType==MSG_TYPE_URL_TEST) {
				int indicatorId = (Integer)map.get("indicatorId");
				JsonMapContext context = new JsonMapContext(map);
				for(TIndicator indi:indicatorList) {
					if(indi.getIndicatorId().intValue()==indicatorId 
							&& indi.getIndicatorType().intValue()==MonitorConstants.INDICATOR_TYPE_URL_TEST 
							&& indi.getEnabled()!=null 
							&& indi.getEnabled().intValue()==1 ) {
						boolean error = MonitorUtil.parseCondition(indi.getErrorCond(), context);
						if(error) GlobalData.setLastErrorLog(indi.getIndicatorId(), map);
						MonitorDataManager.getInstance().addMonitorData(indi.getIndicatorId(), timestamp, error?2:1);
					}
				}
			}
		} catch (Throwable e) {
			logger.error("日志处理失败："+e.getMessage(),e);
		} finally {
			LogDispatcher.getInstance().decrementTaskCount(timestamp);
		}
		
	}

}
