package com.renren.wan.monitor.data;

import java.sql.Timestamp;

import com.renren.wan.monitor.common.MonitorUtil;

public class ChartData {
	
	private int indicatorValue;
	private Timestamp ts;
	
	public String getTime() {
		return MonitorUtil.ts2time(ts.getTime());
	}
	public int getIndicatorValue() {
		return indicatorValue;
	}
	public void setIndicatorValue(int indicatorValue) {
		this.indicatorValue = indicatorValue;
	}
	public void setTs(Timestamp ts) {
		this.ts = ts;
	}
	
}
