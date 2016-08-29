package com.renren.wan.monitor.data;

import java.io.Serializable;
import java.sql.Timestamp;

import com.renren.wan.monitor.common.MonitorUtil;

public class IndicatorData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1687725421235318324L;
	private long id;
	private int indicatorId;
	private int normalCount;
	private int errorCount;
	private Timestamp createTime;        //创建时间
	private int indicatorLevel;     //指标级别：0-正常 1-警告 2-严重
	private String errorInfo;       //出错信息
	private int indicatorValue;
	
	public int getErrorRate() {
		int totalCount = normalCount+errorCount;
		return (totalCount==0)?0:(errorCount*100/totalCount);
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getIndicatorId() {
		return indicatorId;
	}
	public void setIndicatorId(int indicatorId) {
		this.indicatorId = indicatorId;
	}
	
	public int getIndicatorLevel() {
		return indicatorLevel;
	}
	public void setIndicatorLevel(int indicatorLevel) {
		this.indicatorLevel = indicatorLevel;
	}
	public String getErrorInfo() {
		return errorInfo;
	}
	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	public int getNormalCount() {
		return normalCount;
	}

	public void setNormalCount(int normalCount) {
		this.normalCount = normalCount;
	}

	public int getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}

	public int getIndicatorValue() {
		return indicatorValue;
	}

	public void setIndicatorValue(int indicatorValue) {
		this.indicatorValue = indicatorValue;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	
	public String _getTableName() {
		String date = MonitorUtil.ts2date(getCreateTime().getTime());
		return "t_indicator_data_"+date;
	}
	
}
