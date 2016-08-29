package com.renren.wan.monitor.entities;
public class TModule implements java.io.Serializable {
	private static final long serialVersionUID=1L;
	protected java.lang.Integer moduleId = null;
	protected java.lang.String moduleName = null;
	protected java.lang.Integer userId = null;
	protected java.lang.String smsGroup = null;
	protected java.lang.String smsType = null;
	protected java.lang.Integer alertTime = null;
	protected java.lang.Integer alertEnabled = null;

	public TModule() {
	}
	public java.lang.Integer getModuleId() {
		return moduleId;
	}
	public void setModuleId(java.lang.Integer moduleId) {
		 this.moduleId=moduleId;
	}
	public java.lang.String getModuleName() {
		return moduleName;
	}
	public void setModuleName(java.lang.String moduleName) {
		 this.moduleName=moduleName;
	}
	public java.lang.Integer getUserId() {
		return userId;
	}
	public void setUserId(java.lang.Integer userId) {
		 this.userId=userId;
	}
	public java.lang.String getSmsGroup() {
		return smsGroup;
	}
	public void setSmsGroup(java.lang.String smsGroup) {
		 this.smsGroup=smsGroup;
	}
	public java.lang.String getSmsType() {
		return smsType;
	}
	public void setSmsType(java.lang.String smsType) {
		 this.smsType=smsType;
	}
	public java.lang.Integer getAlertTime() {
		return alertTime;
	}
	public void setAlertTime(java.lang.Integer alertTime) {
		 this.alertTime=alertTime;
	}
	public java.lang.Integer getAlertEnabled() {
		return alertEnabled;
	}
	public void setAlertEnabled(java.lang.Integer alertEnabled) {
		 this.alertEnabled=alertEnabled;
	}
}
