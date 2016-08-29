package com.renren.wan.monitor.data;

import java.io.Serializable;

import com.renren.wan.monitor.common.MonitorUtil;

public class MonitorData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3956047137311246664L;
	private int indicatorId;  
	private long createTime;
	private int normalCount = 0;
	private int errorCount = 0;
	
	public int getIndicatorId() {
		return indicatorId;
	}
	public void setIndicatorId(int indicatorId) {
		this.indicatorId = indicatorId;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public int getNormalCount() {
		return normalCount;
	}
	public void setNormalCount(int normalCount) {
		this.normalCount = normalCount;
	}
	public int getErrorCount() {
		return errorCount;
	}
	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}
	
	public String _getTableName() {
		String date = MonitorUtil.ts2date(getCreateTime()*1000);
		return "t_monitor_data_"+date;
	}
}
