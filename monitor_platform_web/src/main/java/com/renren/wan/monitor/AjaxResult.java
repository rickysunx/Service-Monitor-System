package com.renren.wan.monitor;

import net.sf.json.JSONObject;

/**
 * Ajax调用结果封装类
 * @author rui.sun1
 *
 */
public class AjaxResult {
	
	protected boolean success;
	protected String info;
	protected Object data;
	
	protected AjaxResult(boolean success,String info) {
		this.success = success;
		this.info = info;
		this.data = null;
	}
	
	protected AjaxResult(boolean success,String info,Object data) {
		this.success = success;
		this.info = info;
		this.data = data;
	}
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	public static AjaxResult ok() {
		return new AjaxResult(true, "");
	}
	
	public static AjaxResult ok(Object data) {
		return new AjaxResult(true, "",data);
	}
	
	public static AjaxResult fail(String info) {
		return new AjaxResult(false, info);
	}

	@Override
	public String toString() {
		JSONObject json = JSONObject.fromObject(this);
		return json.toString();
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	
}
