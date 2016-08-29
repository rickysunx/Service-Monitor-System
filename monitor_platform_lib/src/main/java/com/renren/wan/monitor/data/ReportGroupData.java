package com.renren.wan.monitor.data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ReportGroupData {
	private int reportGroupId;
	private Timestamp startTime;
	private Timestamp endTime;
	private int moduleId;
	private String moduleName;
	private String logText;
	private List<ReportProcessData> processList = new ArrayList<ReportProcessData>();
	
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
	public List<ReportProcessData> getProcessList() {
		return processList;
	}
	public void setProcessList(List<ReportProcessData> processList) {
		this.processList = processList;
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
	
	public String getLogText() {
		return logText;
	}
	public void setLogText(String logText) {
		this.logText = logText;
	}
	public int getReportGroupId() {
		return reportGroupId;
	}
	public void setReportGroupId(int reportGroupId) {
		this.reportGroupId = reportGroupId;
	}
}
