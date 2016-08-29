package com.renren.wan.monitor.data;

import java.sql.Timestamp;

public class ReportProcessData {
	private Timestamp startTime;
	private Timestamp endTime;
	private int moduleId;
	private String moduleName;
	private int indicatorId;
	private String indicatorName;
	private String logText;
	private int processedFlag;
	private int userId;
	private String userName;
	
	private int pxStart;
	private int pxWidth;
	private int pxMid;
	
	public Timestamp getStartTime() {
		return startTime;
	}
	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}
	public Timestamp getEndTime() {
		return endTime;
	}
	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}
	public int getModuleId() {
		return moduleId;
	}
	public void setModuleId(int moduleId) {
		this.moduleId = moduleId;
	}
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
	public String getLogText() {
		return logText;
	}
	public void setLogText(String logText) {
		this.logText = logText;
	}
	public int getProcessedFlag() {
		return processedFlag;
	}
	public void setProcessedFlag(int processedFlag) {
		this.processedFlag = processedFlag;
	}
	public int getPxStart() {
		return pxStart;
	}
	public void setPxStart(int pxStart) {
		this.pxStart = pxStart;
	}
	public int getPxWidth() {
		return pxWidth;
	}
	public void setPxWidth(int pxWidth) {
		this.pxWidth = pxWidth;
	}
	public int getPxMid() {
		return pxMid;
	}
	public void setPxMid(int pxMid) {
		this.pxMid = pxMid;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
}
