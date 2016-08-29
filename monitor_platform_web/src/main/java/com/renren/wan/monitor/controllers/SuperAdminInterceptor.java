package com.renren.wan.monitor.controllers;

import java.lang.annotation.Annotation;

import com.renren.wan.monitor.annotations.SuperAdminRequired;

import net.paoding.rose.web.ControllerInterceptorAdapter;
import net.paoding.rose.web.Invocation;

public class SuperAdminInterceptor extends ControllerInterceptorAdapter {

	public SuperAdminInterceptor() {
		setPriority(1000);
	}

	@Override
	protected Object before(Invocation inv) throws Exception {
		return true;
	}

	@Override
	protected Class<? extends Annotation> getDenyAnnotationClass() {
		return SuperAdminRequired.class;
	}

}
