package com.renren.wan.monitor.entities;
public class TMonitorData implements java.io.Serializable {
	private static final long serialVersionUID=1L;
	protected java.lang.Integer id = null;
	protected java.lang.Integer indicatorId = null;
	protected java.lang.Integer normalCount = null;
	protected java.lang.Integer errorCount = null;
	protected java.sql.Timestamp createTime = null;

	public TMonitorData() {
	}
	public java.lang.Integer getId() {
		return id;
	}
	public void setId(java.lang.Integer id) {
		 this.id=id;
	}
	public java.lang.Integer getIndicatorId() {
		return indicatorId;
	}
	public void setIndicatorId(java.lang.Integer indicatorId) {
		 this.indicatorId=indicatorId;
	}
	public java.lang.Integer getNormalCount() {
		return normalCount;
	}
	public void setNormalCount(java.lang.Integer normalCount) {
		 this.normalCount=normalCount;
	}
	public java.lang.Integer getErrorCount() {
		return errorCount;
	}
	public void setErrorCount(java.lang.Integer errorCount) {
		 this.errorCount=errorCount;
	}
	public java.sql.Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(java.sql.Timestamp createTime) {
		 this.createTime=createTime;
	}
}
