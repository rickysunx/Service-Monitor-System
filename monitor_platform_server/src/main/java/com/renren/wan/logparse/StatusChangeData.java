package com.renren.wan.logparse;

public class StatusChangeData {
	private int indicatorId=0;
	private int currStatus=0;
	private long lastChangeTime=0;

	public int getIndicatorId() {
		return indicatorId;
	}
	public void setIndicatorId(int indicatorId) {
		this.indicatorId = indicatorId;
	}
	public int getCurrStatus() {
		return currStatus;
	}
	public void setCurrStatus(int currStatus) {
		this.currStatus = currStatus;
	}
	public long getLastChangeTime() {
		return lastChangeTime;
	}
	public void setLastChangeTime(long lastChangeTime) {
		this.lastChangeTime = lastChangeTime;
	}
	
	
}
