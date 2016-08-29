package com.renren.wan.monitor.data;

import java.sql.Timestamp;

public class AlertData {
	private String moduleName;
	private int indicatorId;
	private String indicatorName;
	private String smsGroup;
	private String smsType;
	
	private Timestamp lastStatusTime;
	private int currentValue;
	private int currentStatus;
	private int lastStatus;
	
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public int getIndicatorId() {
		return indicatorId;
	}
	public void setIndicatorId(int indicatorId) {
		this.indicatorId = indicatorId;
	}
	public String getIndicatorName() {
		return indicatorName;
	}
	public void setIndicatorName(String indicatorName) {
		this.indicatorName = indicatorName;
	}
	public String getSmsGroup() {
		return smsGroup;
	}
	public void setSmsGroup(String smsGroup) {
		this.smsGroup = smsGroup;
	}
	public String getSmsType() {
		return smsType;
	}
	public void setSmsType(String smsType) {
		this.smsType = smsType;
	}
	public Timestamp getLastStatusTime() {
		return lastStatusTime;
	}
	public void setLastStatusTime(Timestamp lastStatusTime) {
		this.lastStatusTime = lastStatusTime;
	}
	public int getCurrentValue() {
		return currentValue;
	}
	public void setCurrentValue(int currentValue) {
		this.currentValue = currentValue;
	}
	public int getCurrentStatus() {
		return currentStatus;
	}
	public void setCurrentStatus(int currentStatus) {
		this.currentStatus = currentStatus;
	}
	public int getLastStatus() {
		return lastStatus;
	}
	public void setLastStatus(int lastStatus) {
		this.lastStatus = lastStatus;
	}	
}
