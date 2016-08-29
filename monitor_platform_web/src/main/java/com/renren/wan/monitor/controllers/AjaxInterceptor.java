package com.renren.wan.monitor.controllers;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

import com.renren.wan.monitor.AjaxResult;
import com.renren.wan.monitor.CheckedException;
import com.renren.wan.monitor.Loggers;
import com.renren.wan.monitor.annotations.Ajax;
import net.paoding.rose.web.ControllerInterceptorAdapter;
import net.paoding.rose.web.Invocation;
import net.paoding.rose.web.InvocationChain;

/**
 * 对Ajax请求进行拦截
 * @author rui.sun1
 *
 */
public class AjaxInterceptor extends ControllerInterceptorAdapter {

	public AjaxInterceptor() {
		setPriority(1100);
	}

	@Override
	protected Object round(Invocation inv, InvocationChain chain) throws Exception {
		Object o = null;
		try {
			inv.setAttribute("isAjaxRequest", true);
			o = chain.doNext();
			return AjaxResult.ok(o);
		} catch (InvocationTargetException e) {
			Throwable th = e.getTargetException();
			if(th!=null && th instanceof CheckedException) {
				Loggers.logger.info(th.getMessage());
				return AjaxResult.fail(th.getMessage());
			} else {
				Loggers.logger.error(th.getMessage(),th);
				return AjaxResult.fail(th.getMessage());
			}
		} catch (Exception e) {
			Loggers.logger.error(e.getMessage(), e);
			return AjaxResult.fail(e.getMessage());
		}
	}

	@Override
	protected Class<? extends Annotation> getRequiredAnnotationClass() {
		return Ajax.class;
	}

}
