package com.renren.wan.logparse;

import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.renren.wan.monitor.common.MonitorUtil;
import com.renren.wan.monitor.data.IndicatorData;
import com.renren.wan.monitor.data.MonitorData;
import com.renren.wan.monitor.data.MonitorDayData;
import com.renren.wan.monitor.entities.TIndicator;

public class IndicatorProcessor implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(IndicatorProcessor.class);
	private long timestamp;
	
	public IndicatorProcessor(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public boolean checkCondition(int indicatorValue,String oper,int value) {
		if(oper.equals(">")) {
			if(indicatorValue>value) {
				return true;
			}
		} else if(oper.equals(">=")) {
			if(indicatorValue>=value) {
				return true;
			}
		} else if(oper.equals("<")) {
			if(indicatorValue<value) {
				return true;
			}
		} else if(oper.equals("<=")) {
			if(indicatorValue<=value) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void run() {
		try {
			List<TIndicator> indicatorList = IndicatorManager.getInstance().getIndicatorList();
			
			for(TIndicator indi:indicatorList) {
				try {
					if(indi.getEnabled()!=null && indi.getEnabled()==1) {
						if(timestamp % indi.getSampleRate()==0) {
							int indicatorId = indi.getIndicatorId();
							int indicatorLevel = 0;
							
							//计算正常和错误数
							int statTime = indi.getStatTime();
							int errorType = indi.getErrorType();
							int normalCount = 0;
							int errorCount = 0;
							for(long ts = timestamp-statTime;ts<=timestamp;ts++) {
								MonitorDayData mdd = MonitorDataManager.getInstance().getMonitorDayDataByTs(indicatorId, ts);
								if(mdd!=null) {
									long ts0 = ts*1000;
									normalCount += mdd.getNormalCount(ts0);
									errorCount += mdd.getErrorCount(ts0);
								}
							}
							int indicatorValue = 0;
							if(errorType==1) {
								indicatorValue = errorCount;
							} else if(errorType==2) {
								int totalCount = normalCount+errorCount;
								indicatorValue = ((totalCount==0)?0:(errorCount*100/totalCount));
							}
							//判断警告
							indicatorLevel = checkCondition(indicatorValue, indi.getWarnOperType(), indi.getWarnValue())?1:indicatorLevel;
							
							//判断报警
							boolean shouldAlert = checkCondition(indicatorValue, indi.getAlertOperType(), indi.getAlertValue());
							
							//判断报警最低值
							int minAlertValue = indi.getMinAlertValue()==null?0:indi.getMinAlertValue().intValue();
							if(indicatorValue<minAlertValue) shouldAlert = false;
							indicatorLevel = shouldAlert?2:indicatorLevel;
							
							//----指标数据-----
							IndicatorData indicatorData = new IndicatorData();
							indicatorData.setIndicatorId(indicatorId);
							indicatorData.setNormalCount(normalCount);
							indicatorData.setErrorCount(errorCount);
							indicatorData.setIndicatorValue(indicatorValue);
							indicatorData.setCreateTime(new Timestamp(timestamp*1000));
							indicatorData.setIndicatorLevel(indicatorLevel);
							IndicatorDataManager.getInstance().saveIndicatorData(indicatorData);
							
						}
					}
				} catch (Exception e) {
					logger.error("指标处理出错", e);
				}
			}
		} catch (Exception e) {
			logger.error("加载指标数据出错", e);
		}
		
	}

}
