package com.renren.wan.monitor.data;

import java.util.ArrayList;
import java.util.List;

import com.renren.wan.monitor.entities.TIndicator;
import com.renren.wan.monitor.entities.TModule;


/**
 * 模块数据，包含模块状态和指标列表信息
 * @author rui.sun1
 *
 */
public class ModuleData extends TModule {

	private static final long serialVersionUID = -8819275711840117681L;
	protected int moduleStatus;
	protected List<TIndicator> indicatorList = new ArrayList<TIndicator>();

	public int getModuleStatus() {
		return moduleStatus;
	}

	public void setModuleStatus(int moduleStatus) {
		this.moduleStatus = moduleStatus;
	}

	public List<TIndicator> getIndicatorList() {
		return indicatorList;
	}

	public void setIndicatorList(List<TIndicator> indicatorList) {
		this.indicatorList = indicatorList;
	}

	
	
}
