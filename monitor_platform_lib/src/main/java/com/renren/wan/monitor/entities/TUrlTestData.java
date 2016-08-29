package com.renren.wan.monitor.entities;
public class TUrlTestData implements java.io.Serializable {
	private static final long serialVersionUID=1L;
	protected java.lang.Integer id = null;
	protected java.lang.Integer indicatorId = null;
	protected java.lang.Integer success = null;
	protected java.lang.Integer statusCode = null;
	protected java.lang.Integer spendTime = null;
	protected java.lang.String header = null;
	protected java.lang.String content = null;
	protected java.sql.Timestamp createTime = null;

	public TUrlTestData() {
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
	public java.lang.Integer getSuccess() {
		return success;
	}
	public void setSuccess(java.lang.Integer success) {
		 this.success=success;
	}
	public java.lang.Integer getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(java.lang.Integer statusCode) {
		 this.statusCode=statusCode;
	}
	public java.lang.Integer getSpendTime() {
		return spendTime;
	}
	public void setSpendTime(java.lang.Integer spendTime) {
		 this.spendTime=spendTime;
	}
	public java.lang.String getHeader() {
		return header;
	}
	public void setHeader(java.lang.String header) {
		 this.header=header;
	}
	public java.lang.String getContent() {
		return content;
	}
	public void setContent(java.lang.String content) {
		 this.content=content;
	}
	public java.sql.Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(java.sql.Timestamp createTime) {
		 this.createTime=createTime;
	}
}
