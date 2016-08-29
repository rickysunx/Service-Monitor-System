package com.renren.wan.monitor.common;

//解析获取的参数

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;

import net.sf.json.JSONObject;

public class PropertiesParser {

	JexlContext jexl;
	Expression expression;
	Map<String, Object> map = new HashMap<String, Object>();

	public PropertiesParser() {
		jexl = new JexlContext() {

			@Override
			public void set(String name, Object value) {
				map.put(name, value);
			}

			@Override
			public boolean has(String name) {
				return map.containsKey(name);
			}

			@Override
			public Object get(String name) {
				return map.get(name);
			}
		};
	}

	public PropertiesParser(String formula) throws Exception {
		jexl = new JexlContext() {

			@Override
			public void set(String name, Object value) {
				map.put(name, value);
			}

			@Override
			public boolean has(String name) {
				return map.containsKey(name);
			}

			@Override
			public Object get(String name) {
				return map.get(name);
			}
		};
		setFormula(formula);
	}

	public void setVars(String key, Object obj) {
		jexl.set(key, obj);
	}

	public void setFormula(String formula) throws Exception {
		JexlEngine jexlEngine = new JexlEngine();
		jexlEngine.setSilent(false);
		jexlEngine.setStrict(true);
		expression = jexlEngine.createExpression(formula);
	}

	public Object evaluate() throws Exception {
		return expression.evaluate(jexl);
	}
}



