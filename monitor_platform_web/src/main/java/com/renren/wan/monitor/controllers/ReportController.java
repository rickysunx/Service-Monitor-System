package com.renren.wan.monitor.controllers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.paoding.rose.web.InvocationLocal;
import net.paoding.rose.web.annotation.Param;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.renren.wan.monitor.annotations.LoginRequired;
import com.renren.wan.monitor.common.MonitorUtil;
import com.renren.wan.monitor.dao.ModuleDAO;
import com.renren.wan.monitor.dao.ReportDAO;
import com.renren.wan.monitor.data.ModuleData;
import com.renren.wan.monitor.data.ReportGroupData;
import com.renren.wan.monitor.data.ReportProcessData;

/**
 * 报表
 * @author rui.sun1
 *
 */
public class ReportController {
	private static Logger logger = LoggerFactory.getLogger(IndicatorController.class);
	
	@Autowired
	private InvocationLocal inv;
	
	@Autowired
	private ReportDAO reportDAO;
	
	@Autowired
	private ModuleDAO moduleDAO;
	
	public class ReportDate {
		protected Calendar c = null;
		public ReportDate(long t) {
			c = Calendar.getInstance();
			c.setTimeInMillis(t);
		}
		@Override
		public String toString() {
			return c.get(Calendar.YEAR)+"-"+getXX(c.get(Calendar.MONTH)+1)+"-"+getXX(c.get(Calendar.DATE));
		}
		protected String getXX(int n) {
			return (n<10)?"0"+n:""+n;
		}
		
	};
	
	/**
	 * 报表显示
	 * @param t 
	 * @return
	 */
	public String list(@Param("t")long t) {
		
		if(t==0) t = System.currentTimeMillis();
		long weekStart = getWeekStart(t);
		long weekEnd = getWeekEnd(t);
		List<ReportDate> dateList = new ArrayList<ReportController.ReportDate>();
		for(long i=0;i<7;i++) {
			dateList.add(new ReportDate(weekStart+(i*3600*24*1000)));
		}
		
		List<ModuleData> moduleList = moduleDAO.getAllWithStatus();
		List<ReportProcessData> processData = reportDAO.queryProcessData(new Timestamp(weekStart), new Timestamp(weekEnd));
		
		for(ModuleData md:moduleList) {
			md.setModuleStatus(0);
		}
		
		//合并时间较近的处理数据
		List<ReportGroupData> groupData = new ArrayList<ReportGroupData>();
		for(ReportProcessData data:processData) {
			long startTime = data.getStartTime().getTime();
			long endTime = data.getEndTime().getTime();
			
			boolean inGroup = false;
			
			for(ReportGroupData group:groupData) {
				long g_start = group.getStartTime().getTime();
				long g_end = group.getEndTime().getTime();
				g_end = ((g_end-g_start)>13824000L)?g_end:(g_start+13824000L);
				if(((g_start<=startTime && startTime<=g_end) || (g_start<=endTime && endTime<=g_end)) &&
					group.getModuleId()==data.getModuleId()	) {
					inGroup = true;
					group.getProcessList().add(data);
					g_start = (startTime<g_start)?startTime:g_start;
					g_end = (endTime>g_end)?endTime:g_end;
					
					group.setStartTime(new Timestamp(g_start));
					group.setEndTime(new Timestamp(g_end));
					
				}
			}
			
			if(!inGroup) {
				ReportGroupData group = new ReportGroupData();
				group.setStartTime(data.getStartTime());
				group.setEndTime(data.getEndTime());
				group.setModuleId(data.getModuleId());
				group.setModuleName(data.getModuleName());
				group.getProcessList().add(data);
				groupData.add(group);
			}
			
		}
		
		int groupId = 1;
		
		for(ReportGroupData group:groupData) {
			group.setReportGroupId(groupId++);
			long startTime = group.getStartTime().getTime();
			long endTime = group.getEndTime().getTime();
			int pxStart = (int) ((startTime-weekStart)*700/(3600*24*7*1000));
			long width = (endTime-startTime)*700/(3600*24*7*1000);
			int pxWidth = (int)Math.max(width, 16);
			int pxMid = Math.max(0, pxWidth-16);
			group.setPxStart(pxStart);
			group.setPxWidth(pxWidth);
			group.setPxMid(pxMid);
			
			StringBuffer sb = new StringBuffer();
			
			List<ReportProcessData> processList = group.getProcessList();
			
			if(processList!=null) {
				sb.append("<table class='report_log_table'><tr class='header'><td>报警指标</td><td>处理人</td><td width='70px'>开始时间</td><td width='70px'>结束时间</td><td width='200px'>处理日志</td></tr>");
				for(ReportProcessData data:processList) {
					sb.append("<tr><td>"+data.getIndicatorName()+"</td><td>"+data.getUserName()+"</td><td>"+MonitorUtil.ts2datetime(data.getStartTime().getTime())+
							"</td><td>"+MonitorUtil.ts2datetime(data.getEndTime().getTime())+"</td><td>"+data.getLogText()+"</td></tr>");
				}
				sb.append("</table>");
			}
			
			group.setLogText(sb.toString());
			
			
			for(ModuleData md:moduleList) {
				if(group.getModuleId()==md.getModuleId()) {
					md.setModuleStatus(2);
				}
			}
			
		}
		
		inv.addModel("dateList", dateList);
		inv.addModel("moduleList", moduleList);
		inv.addModel("processData", groupData);
		inv.addModel("prevWeek", t-(3600L*24L*7L*1000L));
		inv.addModel("nextWeek", t+(3600L*24L*7L*1000L));
		
		return "report";
	}
	
	public static void main(String [] args) {
		Calendar c = Calendar.getInstance();
		c.set(2012, 3, 14);
		long now = c.getTimeInMillis();
		System.out.println(new Timestamp(getWeekStart(now)));
		System.out.println(new Timestamp(getWeekEnd(now)));
	}
	
	private static long getWeekStart(long now) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(now);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		for(int i=0;i<7;i++) {
			if(c.get(Calendar.DAY_OF_WEEK)==1) break;
			c.add(Calendar.DATE, -1);
		}
		return c.getTimeInMillis();
	}
	
	private static long getWeekEnd(long now) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(now);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		for(int i=0;i<7;i++) {
			if(c.get(Calendar.DAY_OF_WEEK)==7) break;
			c.add(Calendar.DATE, 1);
		}
		return c.getTimeInMillis();
	}
	
}
