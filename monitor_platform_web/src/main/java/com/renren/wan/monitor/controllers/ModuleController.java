package com.renren.wan.monitor.controllers;

import java.util.List;

import net.paoding.rose.web.annotation.Param;

import org.springframework.beans.factory.annotation.Autowired;

import com.renren.wan.monitor.annotations.Ajax;
import com.renren.wan.monitor.annotations.LoginRequired;
import com.renren.wan.monitor.dao.IndicatorDAO;
import com.renren.wan.monitor.dao.ModuleDAO;
import com.renren.wan.monitor.data.ModuleData;
import com.renren.wan.monitor.entities.TIndicator;
import com.renren.wan.monitor.entities.TModule;

/**
 * 模块处理
 * @author rui.sun1
 *
 */
@LoginRequired
public class ModuleController {
	
	@Autowired
	private ModuleDAO moduleDAO;
	
	@Autowired
	private IndicatorDAO indicatorDAO;
	
	@Ajax
	public void insert(TModule module) {
		moduleDAO.insert(module);
	}
	
	@Ajax
	public void update(TModule module) {
		moduleDAO.update(module);
	}
	
	@Ajax
	public void del(@Param("moduleId")int moduleId) {
		if(moduleDAO.hasIndicator(moduleId)) {
			throw new RuntimeException("请先删除模块下的指标");
		}
		moduleDAO.delete(moduleId);
	}
	
	/**
	 * 获取模块列表，包含指标信息
	 * @return
	 */
	@Ajax
	public List<ModuleData> list() {
		List<ModuleData> moduleList = moduleDAO.getAllForModuleData();
		List<TIndicator> indicatorList = indicatorDAO.getAll();
		
		
		for(ModuleData m:moduleList) {
			for(TIndicator indi:indicatorList) {
				if(m.getModuleId().intValue()==indi.getModuleId().intValue()) {
					m.getIndicatorList().add(indi);
				}
			}
		}
		
		return moduleList;
	}
	
	/**
	 * 获取模块列表，不包含指标信息，但包含状态信息
	 * @return
	 */
	@Ajax
	public List<ModuleData> list0() {
		List<ModuleData> moduleList = moduleDAO.getAllWithStatus();
		return moduleList;
	}
	
}
