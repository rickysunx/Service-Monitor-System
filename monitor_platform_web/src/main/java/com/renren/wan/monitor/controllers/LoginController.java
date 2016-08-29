package com.renren.wan.monitor.controllers;

import net.paoding.rose.web.InvocationLocal;
import net.paoding.rose.web.annotation.Param;

import org.springframework.beans.factory.annotation.Autowired;

import com.renren.wan.monitor.CheckedException;
import com.renren.wan.monitor.StringUtil;
import com.renren.wan.monitor.WebUtil;
import com.renren.wan.monitor.annotations.Ajax;
import com.renren.wan.monitor.common.MonitorUtil;
import com.renren.wan.monitor.dao.UserDAO;
import com.renren.wan.monitor.entities.TUser;

/**
 * 处理登录
 * @author rui.sun1
 */
public class LoginController {
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private InvocationLocal inv;
	
	/**
	 * 登录校验
	 * @return
	 */
	@Ajax
	public Object check(@Param("userName")String userName,@Param("passWord") String passWord) {
		
		StringUtil.checkNull("用户名",userName);
		StringUtil.checkNull("密码", passWord);
		
		TUser user = userDAO.findByLogName(userName);
		
		if(user==null) {
			throw new CheckedException("用户["+userName+"]不存在");
		}
		
		if(user.getPassWord().equalsIgnoreCase(MonitorUtil.md5(passWord))) {
			WebUtil.setLoginUser(inv, user);
		} else {
			throw new CheckedException("密码不正确");
		}		
		return null;
	}
}
