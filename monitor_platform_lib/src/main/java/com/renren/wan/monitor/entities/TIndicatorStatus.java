package com.renren.wan.monitor.entities;
public class TIndicatorStatus implements java.io.Serializable {
	private static final long serialVersionUID=1L;
	protected java.lang.Integer indicatorId = null;
	protected java.sql.Timestamp lastProcessTime = null;
	protected java.sql.Timestamp lastAlertTime = null;
	protected java.sql.Timestamp lastStatusTime = null;
	protected java.lang.Integer currentValue = null;
	protected java.lang.Integer currentStatus = null;
	protected java.lang.Integer lastStatus = null;
	protected java.lang.String lastErrorLog = null;
	protected java.lang.Integer errorAlertCount = null;
	protected java.lang.Integer normalAlertCount = null;

	public TIndicatorStatus() {
	}
	public java.lang.Integer getIndicatorId() {
		return indicatorId;
	}
	public void setIndicatorId(java.lang.Integer indicatorId) {
		 this.indicatorId=indicatorId;
	}
	public java.sql.Timestamp getLastProcessTime() {
		return lastProcessTime;
	}
	public void setLastProcessTime(java.sql.Timestamp lastProcessTime) {
		 this.lastProcessTime=lastProcessTime;
	}
	public java.sql.Timestamp getLastAlertTime() {
		return lastAlertTime;
	}
	public void setLastAlertTime(java.sql.Timestamp lastAlertTime) {
		 this.lastAlertTime=lastAlertTime;
	}
	public java.sql.Timestamp getLastStatusTime() {
		return lastStatusTime;
	}
	public void setLastStatusTime(java.sql.Timestamp lastStatusTime) {
		 this.lastStatusTime=lastStatusTime;
	}
	public java.lang.Integer getCurrentValue() {
		return currentValue;
	}
	public void setCurrentValue(java.lang.Integer currentValue) {
		 this.currentValue=currentValue;
	}
	public java.lang.Integer getCurrentStatus() {
		return currentStatus;
	}
	public void setCurrentStatus(java.lang.Integer currentStatus) {
		 this.currentStatus=currentStatus;
	}
	public java.lang.Integer getLastStatus() {
		return lastStatus;
	}
	public void setLastStatus(java.lang.Integer lastStatus) {
		 this.lastStatus=lastStatus;
	}
	public java.lang.String getLastErrorLog() {
		return lastErrorLog;
	}
	public void setLastErrorLog(java.lang.String lastErrorLog) {
		 this.lastErrorLog=lastErrorLog;
	}
	public java.lang.Integer getErrorAlertCount() {
		return errorAlertCount;
	}
	public void setErrorAlertCount(java.lang.Integer errorAlertCount) {
		 this.errorAlertCount=errorAlertCount;
	}
	public java.lang.Integer getNormalAlertCount() {
		return normalAlertCount;
	}
	public void setNormalAlertCount(java.lang.Integer normalAlertCount) {
		 this.normalAlertCount=normalAlertCount;
	}
}
