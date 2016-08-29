package com.renren.wan.logparse;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.renren.wan.logparse.db.IndicatorDAO;
import com.renren.wan.monitor.entities.TIndicatorProcessLog;
import com.renren.wan.monitor.entities.TIndicatorStatus;

import net.sf.json.JSONObject;

public class GlobalData {
	//最后一条错误日志，key:indicatorId value:errorLog
	private static Map<Integer,Object> lastErrorLogMap = Collections.synchronizedMap(new HashMap<Integer, Object>());
	
	//记录指标当前状态和上次状态变化时间
	private static Map<Integer,StatusChangeData> statusMap = Collections.synchronizedMap(new HashMap<Integer,StatusChangeData>());
	
	//从数据库加载
	public static void loadIndicatorStatusMap() throws Exception {
		List<TIndicatorStatus> statusList = new IndicatorDAO().queryAllStatus();
		for(TIndicatorStatus status:statusList) {
			StatusChangeData scd = new StatusChangeData();
			scd.setIndicatorId(status.getIndicatorId());
			scd.setCurrStatus(status.getCurrentStatus());
			scd.setLastChangeTime(System.currentTimeMillis());
			statusMap.put(status.getIndicatorId(), scd);
		}
	}
	
	//改变指标状态
	public static void changeIndicatorStatus(int indicatorId,int status) throws Exception {
		//System.out.println("changeIndicatorStatus:id:"+indicatorId+" status:"+status);
		long now = System.currentTimeMillis();
		StatusChangeData scd = statusMap.get(indicatorId);
		if(scd==null) {
			scd = new StatusChangeData();
			scd.setIndicatorId(indicatorId);
			scd.setCurrStatus(0);
			scd.setLastChangeTime(now);
			statusMap.put(indicatorId, scd);
		}
		if( (scd.getCurrStatus()==2 && status<2) || (scd.getCurrStatus()<2 && status==2)) {
			System.out.println("scd.currstatus="+scd.getCurrStatus()+" status="+status);
			if(status<2) {
				IndicatorDAO indicatorDAO = new IndicatorDAO();
				
				//指标从报警返回正常，写入处理日志
				TIndicatorStatus indicatorStatus = indicatorDAO.getStatusById(indicatorId);
				
				TIndicatorProcessLog log = new TIndicatorProcessLog();
				log.setIndicatorId(indicatorId);
				log.setUserId(1);
				log.setIndicatorStatus(2);
 				log.setCreateTime(new Timestamp(scd.getLastChangeTime()));
				log.setProcessedTime(new Timestamp(System.currentTimeMillis()));
				log.setProcessedFlag(0);
				log.setLogText("");
				log.setLastErrorLog(indicatorStatus.getLastErrorLog());
				indicatorDAO.insertProcessLog(log);
				indicatorDAO.updateIndicatorStatusTime(indicatorId);
			}
			
			scd.setLastChangeTime(now);
			scd.setCurrStatus(status);
		}
	}
	
	public static void setLastErrorLog(Integer indicatorId,Object log) {
		lastErrorLogMap.put(indicatorId, log);
	}
	
	public static String getLastErrorLogString(Integer indicatorId) {
		Object log = lastErrorLogMap.get(indicatorId);
		if(log==null) return null;
		if(log instanceof String) return (String)log;
		JSONObject json = JSONObject.fromObject(log);
		return json.toString();
	}
}
