package com.renren.wan.monitor.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.paoding.rose.web.InvocationLocal;
import net.paoding.rose.web.annotation.Param;

import org.springframework.beans.factory.annotation.Autowired;

import com.renren.wan.monitor.WebUtil;
import com.renren.wan.monitor.annotations.Ajax;
import com.renren.wan.monitor.annotations.LoginRequired;
import com.renren.wan.monitor.common.MonitorUtil;
import com.renren.wan.monitor.dao.UserDAO;
import com.renren.wan.monitor.entities.TUser;

/**
 * 负责用户处理
 * @author rui.sun1
 *
 */
@LoginRequired
public class UserController {
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private InvocationLocal inv;
	
	@Ajax
	public Map<String, String> insert(TUser user) {
		String passWord = MonitorUtil.getRandomPassWord(6);
		user.setPassWord(MonitorUtil.md5(passWord));
		userDAO.insert(user);
		Map<String, String> result = new HashMap<String, String>();
		result.put("initPass", passWord);
		return result;
	}
	
	@Ajax
	public void del(@Param("id[]")List<Integer> idArray) {
		for(int id:idArray) {
			userDAO.delete(id);
		}
	}
	
	@Ajax
	public void update(TUser user) {
		userDAO.update(user);
	}
	
	@Ajax
	public Object list() {
		return userDAO.getAll();
	}
	
	@Ajax
	public void changePass(@Param("oldPassWord")String oldPassWord,
			@Param("newPassWord")String newPassWord,@Param("newPassWord0") String newPassWord0) {
		
		TUser user0 = WebUtil.getLoginUser(inv);
		TUser user = userDAO.findById(user0.getUserId());
		if(user==null) throw new RuntimeException("用户不存在");
		if(!newPassWord.equals(newPassWord0)) {
			throw new RuntimeException("两次输入的新密码不一致");
		}
		if(!MonitorUtil.md5(oldPassWord).equals(user.getPassWord())) {
			throw new RuntimeException("旧密码不正确");
		}
		userDAO.updatePassword(user.getUserId(), MonitorUtil.md5(newPassWord));
		
	}
	
}
