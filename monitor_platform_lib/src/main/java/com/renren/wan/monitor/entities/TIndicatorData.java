package com.renren.wan.monitor.entities;
public class TIndicatorData implements java.io.Serializable {
	private static final long serialVersionUID=1L;
	protected java.lang.Integer id = null;
	protected java.lang.Integer indicatorId = null;
	protected java.lang.Integer normalCount = null;
	protected java.lang.Integer errorCount = null;
	protected java.lang.Integer indicatorValue = null;
	protected java.sql.Timestamp createTime = null;
	protected java.lang.Integer indicatorLevel = null;
	protected java.lang.String errorInfo = null;

	public TIndicatorData() {
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
	public java.lang.Integer getIndicatorValue() {
		return indicatorValue;
	}
	public void setIndicatorValue(java.lang.Integer indicatorValue) {
		 this.indicatorValue=indicatorValue;
	}
	public java.sql.Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(java.sql.Timestamp createTime) {
		 this.createTime=createTime;
	}
	public java.lang.Integer getIndicatorLevel() {
		return indicatorLevel;
	}
	public void setIndicatorLevel(java.lang.Integer indicatorLevel) {
		 this.indicatorLevel=indicatorLevel;
	}
	public java.lang.String getErrorInfo() {
		return errorInfo;
	}
	public void setErrorInfo(java.lang.String errorInfo) {
		 this.errorInfo=errorInfo;
	}
}
