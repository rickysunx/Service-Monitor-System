package com.renren.wan.monitor.controllers;

import org.springframework.beans.factory.annotation.Autowired;

import com.renren.wan.monitor.WebUtil;

import net.paoding.rose.web.InvocationLocal;
import net.paoding.rose.web.annotation.Path;
import net.paoding.rose.web.annotation.rest.Get;

/**
 * 框架控制器
 * @author rui.sun1
 */
@Path("")
public class FrameController {
	
	@Autowired
	private InvocationLocal inv;
	
	/**
	 * 首页
	 * @return
	 */
	@Get("")
	public String index() {
		if(WebUtil.isLogin(inv)) {
			return "frame";
		} else {
			return "r:/login";
		}
	}
	
	/**
	 * 登录页
	 * @return
	 */
	public String login() {
		return "login";
	}
	
	/**
	 * 退出系统登录
	 * @return
	 */
	public String exit() {
		WebUtil.setLoginUser(inv, null);
		return "r:/";
	}
	
	public String report() {
		return "report_frame";
	}
}
