package com.renren.wan.monitor.entities;
public class TUser implements java.io.Serializable {
	private static final long serialVersionUID=1L;
	protected java.lang.Integer userId = null;
	protected java.lang.String logName = null;
	protected java.lang.String userName = null;
	protected java.lang.String passWord = null;
	protected java.lang.String mobile = null;
	protected java.lang.String email = null;

	public TUser() {
	}
	public java.lang.Integer getUserId() {
		return userId;
	}
	public void setUserId(java.lang.Integer userId) {
		 this.userId=userId;
	}
	public java.lang.String getLogName() {
		return logName;
	}
	public void setLogName(java.lang.String logName) {
		 this.logName=logName;
	}
	public java.lang.String getUserName() {
		return userName;
	}
	public void setUserName(java.lang.String userName) {
		 this.userName=userName;
	}
	public java.lang.String getPassWord() {
		return passWord;
	}
	public void setPassWord(java.lang.String passWord) {
		 this.passWord=passWord;
	}
	public java.lang.String getMobile() {
		return mobile;
	}
	public void setMobile(java.lang.String mobile) {
		 this.mobile=mobile;
	}
	public java.lang.String getEmail() {
		return email;
	}
	public void setEmail(java.lang.String email) {
		 this.email=email;
	}
}
