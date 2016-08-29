package com.renren.wan.monitor.common;

import java.util.Map;

import org.apache.commons.jexl2.JexlContext;

import net.sf.json.JSONObject;

public class JsonMapContext implements JexlContext {

	private JSONObject json = null;
	private Map<String, Object> map = null; 
	
	public JsonMapContext(String json) {
		this.json = JSONObject.fromObject(json);
	}
	
	public JsonMapContext(JSONObject json) {
		this.json = json;
	}
	
	public JsonMapContext(Map<String, Object> map) {
		this.map = map;
	}

	@Override
	public Object get(String name) {
		if(json!=null) {
			return json.get(name);
		} else if (map!=null) {
			return map.get(name);
		}
		return null;
	}

	@Override
	public void set(String name, Object value) {
		if(json!=null) {
			json.element(name, value);
		} else if (map!=null) {
			map.put(name, value);
		}
	}

	@Override
	public boolean has(String name) {
		if(json!=null) {
			return json.has(name);
		} else if(map!=null) {
			return map.containsKey(name);
		}
		return false;
	}

}
