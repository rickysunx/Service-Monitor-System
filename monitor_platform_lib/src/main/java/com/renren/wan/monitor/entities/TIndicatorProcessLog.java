package com.renren.wan.monitor.entities;
public class TIndicatorProcessLog implements java.io.Serializable {
	private static final long serialVersionUID=1L;
	protected java.lang.Integer logid = null;
	protected java.lang.Integer indicatorId = null;
	protected java.lang.Integer userId = null;
	protected java.lang.Integer indicatorStatus = null;
	protected java.sql.Timestamp createTime = null;
	protected java.sql.Timestamp processedTime = null;
	protected java.lang.Integer processedFlag = null;
	protected java.lang.String logText = null;
	protected java.lang.String lastErrorLog = null;

	public TIndicatorProcessLog() {
	}
	public java.lang.Integer getLogid() {
		return logid;
	}
	public void setLogid(java.lang.Integer logid) {
		 this.logid=logid;
	}
	public java.lang.Integer getIndicatorId() {
		return indicatorId;
	}
	public void setIndicatorId(java.lang.Integer indicatorId) {
		 this.indicatorId=indicatorId;
	}
	public java.lang.Integer getUserId() {
		return userId;
	}
	public void setUserId(java.lang.Integer userId) {
		 this.userId=userId;
	}
	public java.lang.Integer getIndicatorStatus() {
		return indicatorStatus;
	}
	public void setIndicatorStatus(java.lang.Integer indicatorStatus) {
		 this.indicatorStatus=indicatorStatus;
	}
	public java.sql.Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(java.sql.Timestamp createTime) {
		 this.createTime=createTime;
	}
	public java.sql.Timestamp getProcessedTime() {
		return processedTime;
	}
	public void setProcessedTime(java.sql.Timestamp processedTime) {
		 this.processedTime=processedTime;
	}
	public java.lang.Integer getProcessedFlag() {
		return processedFlag;
	}
	public void setProcessedFlag(java.lang.Integer processedFlag) {
		 this.processedFlag=processedFlag;
	}
	public java.lang.String getLogText() {
		return logText;
	}
	public void setLogText(java.lang.String logText) {
		 this.logText=logText;
	}
	public java.lang.String getLastErrorLog() {
		return lastErrorLog;
	}
	public void setLastErrorLog(java.lang.String lastErrorLog) {
		 this.lastErrorLog=lastErrorLog;
	}
}
