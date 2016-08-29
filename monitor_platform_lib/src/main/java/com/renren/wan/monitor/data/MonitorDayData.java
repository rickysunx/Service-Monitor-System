package com.renren.wan.monitor.data;

import java.io.Serializable;

import com.renren.wan.monitor.common.MonitorUtil;

/**
 * 单日监控数据
 * @author rui.sun1
 *
 */
public class MonitorDayData implements Serializable {
	private static final long serialVersionUID = 6097102953278929L;

	private int indicatorId;
	private int days;
	private int [] data;
	private boolean free;
	
	public MonitorDayData() {
		data = new int[24*3600*2];
		free = true;
	}
	
	public void free() {
		for(int i=0;i<data.length;i++) {
			data[i] = 0;
		}
		free = true;
	}
	
	public void setDate(int days) {
		this.days = days;
	}
	
	public int getDate() {
		return days;
	}
	
	public int getIndexByTs(long ts) {
		long ts_start = MonitorUtil.getDayStartTs(ts);
		return ((int)((ts-ts_start)/1000))*2;
	}
	
	public int getNormalCount(long ts) {
		int index = getIndexByTs(ts);
		return data[index];
	}
	
	public int getErrorCount(long ts) {
		int index = getIndexByTs(ts);
		return data[index+1];
	}
	
	public void setNormalCount(long ts,int normalCount) {
		int index = getIndexByTs(ts);
		data[index] = normalCount;
	}
	
	public void setErrorCount(long ts,int errorCount) {
		int index = getIndexByTs(ts);
		data[index+1] = errorCount;
	}
	
	public void incrementNormalCount(long ts) {
		int index = getIndexByTs(ts);
		data[index] = data[index]+1;
	}
	
	public void incrementErrorCount(long ts) {
		int index = getIndexByTs(ts);
		data[index+1] = data[index+1]+1;
	}
	
	
	public int getIndicatorId() {
		return indicatorId;
	}

	public void setIndicatorId(int indicatorId) {
		this.indicatorId = indicatorId;
	}

	public boolean isFree() {
		return free;
	}

	public void setFree(boolean free) {
		this.free = free;
	}
	
}
