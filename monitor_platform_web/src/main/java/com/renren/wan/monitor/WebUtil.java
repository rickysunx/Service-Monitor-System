package com.renren.wan.monitor;

import javax.servlet.http.HttpSession;

import net.paoding.rose.web.Invocation;

import com.renren.wan.monitor.common.MonitorConstants;
import com.renren.wan.monitor.entities.TUser;

public class WebUtil {
	/**
	 * 判断用户是否登录
	 * @param inv
	 * @return
	 */
	public static boolean isLogin(Invocation inv) {
		HttpSession session =  inv.getRequest().getSession();
		Object o = session.getAttribute(MonitorConstants.SESSION_NAME);
		return o!=null;
	}
	
	
	/**
	 * 设置登录用户
	 * @param inv
	 * @param user
	 */
	public static void setLoginUser(Invocation inv,TUser user) {
		HttpSession session =  inv.getRequest().getSession();
		session.setAttribute(MonitorConstants.SESSION_NAME, user);
	}
	
	/**
	 * 获取登录用户
	 * @param inv
	 * @return
	 */
	public static TUser getLoginUser(Invocation inv) {
		HttpSession session =  inv.getRequest().getSession();
		return (TUser)session.getAttribute(MonitorConstants.SESSION_NAME);
	}
}
