package com.renren.wan.monitor.dao;

import java.util.List;

import com.renren.wan.monitor.data.ModuleData;
import com.renren.wan.monitor.entities.TModule;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;

/**
 * 模块操作DAO
 * @author rui.sun1
 *
 */
@DAO
public interface ModuleDAO {
	
	@SQL("select moduleId,moduleName,userId,smsGroup,smsType,alertTime,alertEnabled from t_module order by moduleName")
	public List<TModule> getAll();
	
	@SQL("select moduleId,moduleName,userId,smsGroup,smsType,alertTime,alertEnabled from t_module order by moduleName")
	public List<ModuleData> getAllForModuleData();
	
	@SQL("insert into t_module(moduleName,userId,smsGroup,smsType,alertTime,alertEnabled) values (:1.moduleName,:1.userId,:1.smsGroup,:1.smsType,:1.alertTime,:1.alertEnabled) ")
	public int insert(TModule module);
	
	@SQL("update t_module set moduleName=:1.moduleName,userId=:1.userId,smsGroup=:1.smsGroup,smsType=:1.smsType,alertTime=:1.alertTime,alertEnabled=:1.alertEnabled where moduleId=:1.moduleId ")
	public void update(TModule module);
	
	@SQL("delete from t_module where moduleId=:1")
	public void delete(int moduleId);
	
	//查询所有模块，带当前状态信息
	@SQL("SELECT moduleId,moduleName, "+
		"(SELECT if(MAX(indistatus.lastStatus),MAX(indistatus.lastStatus),0)  "+
		"FROM t_indicator indi,t_indicator_status indistatus  "+
		"WHERE indi.indicatorId = indistatus.indicatorId AND indi.moduleId=m.moduleId) moduleStatus "+
		"FROM t_module m "+
		"ORDER BY moduleName ")
	public List<ModuleData> getAllWithStatus();
	
	@SQL("select moduleId,moduleName,userId,smsGroup,smsType,alertTime,alertEnabled from t_module where moduleId=:1")
	public TModule getById(int moduleId);
	
	@SQL("select count(1)>0 from t_indicator where moduleId=:1")
	public boolean hasIndicator(int moduleId);
	
}
