package com.renren.wan.monitor.entities;

import java.io.Serializable;
import java.sql.Timestamp;

public class LogData implements Serializable{
	private static final long serialVersionUID = 1L;
	private int id;
	private int logId;
	private String userId;
	private String userName;
	private String gameDomain;
	private Timestamp createTime;
	private int reason;
	private String logType;
	private String extension;
	private String properties;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getLogId() {
		return logId;
	}
	public void setLogId(int logId) {
		this.logId = logId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getGameDomain() {
		return gameDomain;
	}
	public void setGameDomain(String gameDomain) {
		this.gameDomain = gameDomain;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public int getReason() {
		return reason;
	}
	public void setReason(int reason) {
		this.reason = reason;
	}
	public String getLogType() {
		return logType;
	}
	public void setLogType(String logType) {
		this.logType = logType;
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	public String getProperties() {
		return properties;
	}
	public void setProperties(String properties) {
		this.properties = properties;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	@Override
	public String toString() {
		return "LogData [logId=" + logId + ", userId=" + userId + ", userName="
				+ userName + ", gameDomain=" + gameDomain + ", createTime="
				+ createTime + ", reason=" + reason + ", logType=" + logType
				+ ", extension=" + extension + ", properties=" + properties
				+ "]";
	}
	
	
}
