package com.renren.wan.monitor.controllers;

import java.lang.annotation.Annotation;

import com.renren.wan.monitor.CheckedException;
import com.renren.wan.monitor.WebUtil;
import com.renren.wan.monitor.annotations.LoginRequired;

import net.paoding.rose.web.ControllerInterceptorAdapter;
import net.paoding.rose.web.Invocation;

public class LoginInterceptor extends ControllerInterceptorAdapter {

	public LoginInterceptor() {
		setPriority(850);
	}

	@Override
	protected Object before(Invocation inv) throws Exception {
		Boolean bAjaxRequest = (Boolean)inv.getAttribute("isAjaxRequest");
		if(!WebUtil.isLogin(inv)) {
			if(bAjaxRequest!=null && bAjaxRequest) {
				throw new CheckedException("未登录");
			} else {
				return "r:/";
			}
		}
		return true;
	}

	@Override
	protected Class<? extends Annotation> getRequiredAnnotationClass() {
		return LoginRequired.class;
	}

}
