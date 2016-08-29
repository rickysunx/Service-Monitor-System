package com.renren.wan.monitor.data;

import com.renren.wan.monitor.common.MonitorUtil;
import com.renren.wan.monitor.entities.TIndicatorStatus;

public class IndicatorStatusData extends TIndicatorStatus {
	
	private static final long serialVersionUID = 4046130848571970188L;
	protected int moduleId;
	protected String moduleName;
	protected String indicatorName;
	protected int alertValue;
	protected int warnValue;
	protected int errorType;
	
	public String getCurrentStatusHtml() {
		return MonitorUtil.getIndicatorStatusHtml(this.currentStatus);
	}
	public String getLastStatusHtml() {
		return MonitorUtil.getIndicatorStatusHtml(this.lastStatus);
	}
	public String getOperationHtml() {
		String s = "<a href=\"javascript:showIndicatorDetail("+this.indicatorId+",'"+this.indicatorName+"');\" class='monitor_operation'>查看趋势</a> ";
		
		if(lastStatus!=null && lastStatus>0) {
			s+="<a href=\"javascript:processException("+this.moduleId+","+this.indicatorId+",'"+this.indicatorName+"');\" class='monitor_operation'>处理异常</a> ";
			s+="<a href=\"javascript:showLastErrorLog("+this.indicatorId+");\" class='monitor_operation'>查看出错日志</a>";
		}
		return s;
	}
	
	
	public int getModuleId() {
		return moduleId;
	}
	public void setModuleId(int moduleId) {
		this.moduleId = moduleId;
	}
	public String getIndicatorName() {
		return indicatorName;
	}
	public void setIndicatorName(String indicatorName) {
		this.indicatorName = indicatorName;
	}
	
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public int getAlertValue() {
		return alertValue;
	}
	public void setAlertValue(int alertValue) {
		this.alertValue = alertValue;
	}
	public int getWarnValue() {
		return warnValue;
	}
	public void setWarnValue(int warnValue) {
		this.warnValue = warnValue;
	}
	
	private String getValueSuffix() {
		return errorType==1?"次":"%";
	}
	
	public String getCurrentValueName() {
		return getCurrentValue()+getValueSuffix();
	}
	
	public String getWarnValueName() {
		return getWarnValue()+getValueSuffix();
	}
	
	public String getAlertValueName() {
		return getAlertValue()+getValueSuffix();
	}
	
	public String getLastStatusTimeName() {
		if(this.lastStatus!=null && this.lastStatus>=1) {
			if(this.lastStatusTime!=null) return MonitorUtil.ts2datetime(this.lastStatusTime.getTime());
		}
		return "";
	}
	public int getErrorType() {
		return errorType;
	}
	public void setErrorType(int errorType) {
		this.errorType = errorType;
	}
}
