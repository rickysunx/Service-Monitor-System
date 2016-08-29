package com.renren.wan.monitor.dao;

import java.util.List;

import com.renren.wan.monitor.entities.TUser;


import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;

/**
 * 用户相关DAO操作
 * @author rui.sun1
 *
 */
@DAO
public interface UserDAO {
	
	@SQL("select userId,logName,userName,passWord,mobile,email from t_user where logName=:1")
	public TUser findByLogName(String logName);
	
	@SQL("select userId,logName,userName,passWord,mobile,email from t_user where userId=:1")
	public TUser findById(int userId);
	
	@SQL("select userId,logName,userName,passWord,mobile,email from t_user order by userId")
	public List<TUser> getAll();
	
	@SQL("insert into t_user(logName,userName,passWord,mobile,email) values (:1.logName,:1.userName,:1.passWord,:1.mobile,:1.email) ")
	public void insert(TUser user);
	
	@SQL("update t_user set logName=:1.logName,userName=:1.userName,passWord=:1.passWord,mobile=:1.mobile,email=:1.email where userId=:1.userId ")
	public void update(TUser user);
	
	@SQL("delete from t_user where userId=:1")
	public void delete(int id);
	
	@SQL("update t_user set passWord=:2 where userId=:1")
	public void updatePassword(int id,String password);
	
}
