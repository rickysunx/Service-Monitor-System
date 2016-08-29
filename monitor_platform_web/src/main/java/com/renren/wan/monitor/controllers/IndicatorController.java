package com.renren.wan.monitor.controllers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import net.paoding.rose.web.InvocationLocal;
import net.paoding.rose.web.annotation.Param;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleInsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.renren.wan.monitor.AjaxResult;
import com.renren.wan.monitor.WebUtil;
import com.renren.wan.monitor.annotations.Ajax;
import com.renren.wan.monitor.annotations.LoginRequired;
import com.renren.wan.monitor.common.JsonMapContext;
import com.renren.wan.monitor.common.MonitorConstants;
import com.renren.wan.monitor.common.MonitorUtil;
import com.renren.wan.monitor.common.PropertiesParser;
import com.renren.wan.monitor.dao.IndicatorDAO;
import com.renren.wan.monitor.dao.LogCenterDAO;
import com.renren.wan.monitor.dao.ModuleDAO;
import com.renren.wan.monitor.data.IndicatorData;
import com.renren.wan.monitor.data.IndicatorStatusData;
import com.renren.wan.monitor.data.TableListData;
import com.renren.wan.monitor.entities.LogData;
import com.renren.wan.monitor.entities.TIndicator;
import com.renren.wan.monitor.entities.TIndicatorProcessLog;
import com.renren.wan.monitor.entities.TIndicatorStatus;
import com.renren.wan.monitor.entities.TModule;
import com.renren.wan.monitor.entities.TUrlTestData;
/**
 * 指标处理
 * @author rui.sun1
 *
 */
@LoginRequired
public class IndicatorController {
	
	private static Logger logger = LoggerFactory.getLogger(IndicatorController.class);
	
	@Autowired
	private IndicatorDAO indicatorDAO;
	
	@Autowired
	private LogCenterDAO logCenterDAO;
	
	@Autowired
	private ModuleDAO moduleDAO;
	
	@Autowired
	private InvocationLocal inv;
	
	@Ajax
	public void insert(TIndicator indicator) {
		indicatorDAO.insert(indicator);
	}
	
	@Ajax
	public void update(TIndicator indicator) {
		indicatorDAO.update(indicator);
	}
	
	@Ajax
	public void del(@Param("indicatorId")int indicatorId) {
		indicatorDAO.delete(indicatorId);
	}
	
	@Ajax
	public List<TableListData> tables() {
		return logCenterDAO.getTables();
	}
	
	/**
	 * 根据模块ID查找指标，包含状态信息
	 * @param moduleId
	 * @return
	 */
	@Ajax
	public List<IndicatorStatusData> list(@Param("moduleId")int moduleId) {
		return indicatorDAO.getStatusByModuleId(moduleId);
	}

	
	/**
	 * 处理异常
	 * @param processLog
	 */
	//获取处理异常时异常已经恢复正常的时间
	public Timestamp getlatestNormalTime(int indicatorId,Timestamp lastStatusTime){
		String date = lastStatusTime.toString().substring(0, 4)+lastStatusTime.toString().substring(5, 7)+lastStatusTime.toString().substring(8, 10);
		String tableName = "t_indicator_data_"+date;
		
		int year = Integer.valueOf(lastStatusTime.toString().substring(0, 4));
		int month = Integer.valueOf(lastStatusTime.toString().substring(5, 7));
		int day = Integer.valueOf(lastStatusTime.toString().substring(8, 10));
		int hour =  Integer.valueOf(lastStatusTime.toString().substring(11, 13));
		int minute = Integer.valueOf(lastStatusTime.toString().substring(14, 16));
		int second = Integer.valueOf(lastStatusTime.toString().substring(17, 19));
		
		Timestamp startTs = MonitorUtil.createTimestamp(year, month, day, hour, minute, second);
		Timestamp endTs = MonitorUtil.createTimestamp(year, month, day, 23, 59, 59);
		
		Timestamp latestNormalTime = null;
		if(indicatorDAO.tableExists(tableName)) {
			latestNormalTime = indicatorDAO.getLastNormalTime(indicatorId, startTs, endTs,tableName);
		}
		return latestNormalTime;
	}
	@Ajax
	public void process(TIndicatorProcessLog processLog) {
		Timestamp now = new Timestamp(System.currentTimeMillis()/1000*1000);
		TIndicatorStatus status = indicatorDAO.getStatusById(processLog.getIndicatorId());
		processLog.setIndicatorStatus(status.getLastStatus());
		processLog.setUserId(WebUtil.getLoginUser(inv).getUserId());
		processLog.setCreateTime(status.getLastStatusTime());
		
		Timestamp nromalTime = getlatestNormalTime(processLog.getIndicatorId(),status.getLastStatusTime());
		if(nromalTime==null){
			processLog.setProcessedTime(now);
		}else{
			processLog.setProcessedTime(nromalTime);
		}
		
		processLog.setProcessedFlag(1);
		processLog.setLastErrorLog(status.getLastErrorLog());
		indicatorDAO.insertProcessLog(processLog);
		indicatorDAO.updateRecentProcessLog(processLog);
		indicatorDAO.updateStatusForProcess(processLog.getIndicatorId(), now);
		
		TIndicator indicator = indicatorDAO.getById(status.getIndicatorId());
		TModule module = moduleDAO.getById(indicator.getModuleId());
		
		try {
			if(module.getAlertEnabled()!=null && module.getAlertEnabled()==1) {
				sendSms("平台监控:["+WebUtil.getLoginUser(inv).getUserName()+
					"]处理了模块["+module.getModuleName()+"]的["+indicator.getIndicatorName()+"]指标",
					module.getSmsType(), module.getSmsGroup());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
	}
	/*
	 * 查询出错日志
	 */
	//获取出错类型及查询信息
	@Ajax
	public JSONObject getindicatorInfo(@Param("indicatorId")int indicatorId){
		JSONObject jsObject = new  JSONObject();
		TIndicator tIndicator = indicatorDAO.getById(indicatorId);
		
		//获取查询时间
		// 首先查询上次报警时间的信息
		TIndicatorStatus tIndicatorStatus = indicatorDAO.getStatusById(indicatorId);
		Timestamp startTimestamp = tIndicatorStatus.getLastAlertTime();
		// 获取现在时间的毫秒数
		long endMiliTime = System.currentTimeMillis();
		Timestamp endTimestamp = new Timestamp(endMiliTime);
		
		
		jsObject.accumulate("indicatorType", tIndicator.getIndicatorType());
		jsObject.accumulate("startTime", startTimestamp);
		jsObject.accumulate("endTime", endTimestamp);
		
		return jsObject;
	}
	
	
	@Ajax
	public JSONObject getErrorLog(@Param("indicatorId")int indicatorId,@Param("lastRecord")String lastRecord,@Param("queryCond") String queryCond,@Param("jexlCond") String jexlCond)throws Exception{
		
		// 首先查询上次报警时间的信息
		TIndicatorStatus tIndicatorStatus = indicatorDAO.getStatusById(indicatorId);
		Timestamp startTimestamp = tIndicatorStatus.getLastAlertTime();
		// 获取现在时间的毫秒数
		long endMiliTime = System.currentTimeMillis();
		Timestamp endTimestamp = new Timestamp(endMiliTime);

		// 获取查询log所需要的条件
		TIndicator tIndicator = indicatorDAO.getById(indicatorId);
		// 确定时间列的名称
		String columnName = tIndicator.getIndicatorType() == 0 ? "create_time": "createTime";


		// 检查输入的命令行里面是不是有时间范围
		long startMillTime = startTimestamp.getTime();
		List<String> list = new ArrayList<String>();
		if (queryCond != null) {
			Pattern p1 = Pattern.compile("ime [>=<]* *'\\d{4}-\\d{1,2}-\\d{1,2} *\\d{1,2}:\\d{1,2}:\\d{1,2}'");
			Matcher m1 = p1.matcher(queryCond);
			while (m1.find()) {
				String temp = m1.group();
				list.add(temp);
			}
			// 如果有日期型的条件
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Pattern p2 = Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2} *\\d{1,2}:\\d{1,2}:\\d{1,2}");
					Matcher m2 = p2.matcher(list.get(i));
					String str = null;
					if (m2.find()) {
						str = m2.group();
					}
					long time = MonitorUtil.getMiliTime(str);
					if (list.get(i).contains("<")) {
						endMiliTime = time;
					}
					if (list.get(i).contains(">")) {
						startMillTime = time;
					}
				}
			}
		}
		if (queryCond==null||queryCond.length()<3) {
			queryCond = columnName +">= "+startTimestamp.toString()+" and "+columnName+"<= "+endTimestamp.toString();
		} else {
			if (list.size() > 0) {
				String str = "";
				for (int i = 0; i < list.size(); i++) {
					str += (list.get(i) + " ");
				}
				if (!(str.contains("<") && str.contains(">"))) {
					String temp = queryCond;
					if (str.contains("<")) {
						queryCond = columnName + ">= '"+ startTimestamp.toString() + "' and " + temp;
					}
					if (str.contains(">")) {
						queryCond = columnName + "<= '"+ endTimestamp.toString() + "' and " + temp;
					}
				}
			}
		}		
		// 确定需要从哪些表中获取数据
		List<String> tableNameList = new ArrayList<String>();
		startTimestamp.setTime(startMillTime);
		endTimestamp.setTime(endMiliTime);
		while (Integer.valueOf(MonitorUtil.ts2date(startMillTime)) <= Integer.valueOf(MonitorUtil.ts2date(endMiliTime))) {
			Timestamp timestamp = new Timestamp(startMillTime);
			String year = timestamp.toString().substring(0, 4);
			String month = timestamp.toString().substring(5, 7);
			String date = timestamp.toString().substring(8, 10);
			if (tIndicator.getIndicatorType() == 0) {
				String tableName = "platform_log_" + year + "_" + month + "_"+ date;
				if (logCenterDAO.tableExists(tableName)) {
					tableNameList.add(tableName);
				}
			}
			if (tIndicator.getIndicatorType() == 1) {
				String tableName = "t_url_test_data_" + year + month + date;
				if (indicatorDAO.tableExists(tableName)) {
					tableNameList.add(tableName);
				}
			}
			startMillTime += 86400000L;
		}
		//将传入的参数展成json对象
		
		JSONObject lastRecordJson = null;
		if(lastRecord!=null&&lastRecord.length()>3){
			lastRecordJson = JSONObject.fromObject(lastRecord);
			//System.out.println("lastRecord-----"+lastRecord);
		}
		
		
		
		//日志类型标志
		int logType = tIndicator.getIndicatorType();
		
		JSONArray jArray = new JSONArray();
		if (jexlCond==null||jexlCond.length()<3) {
			int i=0;
			int delta = 1;
			boolean ctrl = true;
			if(lastRecordJson!=null){
				i=lastRecordJson.getInt("tableId");
				if(lastRecordJson.getInt("updown")==0){
					delta=-1;
					ctrl = i>=0;
				}else{
					ctrl = i<tableNameList.size();
				}
			}
			while(ctrl) {
				String tableName = tableNameList.get(i);
				i += delta;
				if(i<0||i>tableNameList.size()-1){
					ctrl = false; 
				}
				if(logType==0){
					int begin = 0;
					List<LogData> tempList = new ArrayList<LogData>();
					do {
						String cond = queryCond;
						if(lastRecordJson!=null){
							if(lastRecordJson.getInt("updown")==0){
								ctrl = i>=0;
								if(tableName.equals(lastRecordJson.getString("tableName"))){
									cond = queryCond+" and id<"+lastRecordJson.getInt("id")+" order by id desc";
								}else{
									cond = queryCond+" order by id desc";
								}
							}else{
								ctrl = i<tableNameList.size();
								if(tableName.equals(lastRecordJson.getString("tableName"))){
									cond = queryCond+" and id>"+lastRecordJson.getInt("id");
								}
							}
						}
						//分批查询，每批数量为30000个，否则会造成堆栈溢出
						tempList = logCenterDAO.getLogData(tableName, cond,begin, 30000);
						for (int j = 0; j < tempList.size(); j++) {
							LogData logData = tempList.get(j);
							if (logData.getProperties().contains("\n")) {
								logData.setProperties(logData.getProperties().replace("\n", ""));
							}
							JSONObject json = JSONObject.fromObject(logData);
							json.remove("createTime");
							json.accumulate("createTime", logData.getCreateTime().toString());
							json.remove("properties");
							json.accumulate("properties", logData.getProperties().toString() + " ");
							json.accumulate("tableId", tableNameList.indexOf(tableName));
							json.accumulate("tableName", tableName);
							jArray.add(json);
							if (jArray.size() > 9) {
								break;
							}
						}
						begin += 30000;
					} while (jArray.size() < 10&&tempList.size()!=0);
				}else{
					int begin =0;
					List<TUrlTestData> tempList = new ArrayList<TUrlTestData>();
					do {
						String cond = queryCond;
						if(lastRecordJson!=null){
							if(lastRecordJson.getInt("updown")==0){
								ctrl = i>=0;
								if(tableName.equals(lastRecordJson.getString("tableName"))){
									cond = queryCond+" and id<"+lastRecordJson.getInt("id")+" order by id desc";
								}else{
									cond = queryCond+" order by id desc";
								}
							}else{
								ctrl = i<tableNameList.size();
								if(tableName.equals(lastRecordJson.getString("tableName"))){
									cond = queryCond+" and id>"+lastRecordJson.getInt("id");
								}
							}
						}
						tempList = indicatorDAO.getUrlTest(tableName, indicatorId,cond, begin, 30000);
						for (int j = 0; j < tempList.size(); j++) {
							TUrlTestData tUrlTestData = tempList.get(j);
							if (tUrlTestData.getHeader()!=null&&tUrlTestData.getHeader().length()>0) {
								if (tUrlTestData.getHeader().contains("\n")) {
									tUrlTestData.setHeader(tUrlTestData
											.getHeader().replace("\n", ""));
								}
							}
							JSONObject json = JSONObject.fromObject(tUrlTestData);
							json.remove("createTime");
							json.accumulate("createTime", tUrlTestData.getCreateTime().toString());
							json.remove("header");
							if (tUrlTestData.getHeader()!=null&&tUrlTestData.getHeader().length()>0) {
								json.accumulate("header", tUrlTestData.getHeader().toString() + " ");
							}
							json.accumulate("tableId", tableNameList.indexOf(tableName));
							json.accumulate("tableName", tableName);
							jArray.add(json);
							if (jArray.size() > 9) {
								break;
							}
						}
						begin +=30000;
					} while (jArray.size() < 10&&tempList.size()!=0);
				}
				if(jArray.size()>9){
					break;
				}
			}
			//如果按的是上一页，则把数据倒序过来
			if(lastRecordJson!=null&&lastRecordJson.getInt("updown")==0){
				JSONArray tempArray = new JSONArray();
				for(int k=jArray.size()-1;k>=0;k--){
					tempArray.add(jArray.get(k));
				}
				jArray = tempArray;
			}
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate("logs", jArray);
			return jsonObject;
		}else{
			int i=0;
			int delta = 1;
			boolean ctrl = true;
			if(lastRecordJson!=null){
				i=lastRecordJson.getInt("tableId");
				if(lastRecordJson.getInt("updown")==0){
					delta=-1;
					ctrl = i>=0;
				}else{
					ctrl = i<tableNameList.size();
				}
			}
			while(ctrl) {
				String tableName = tableNameList.get(i);
				i += delta;
				if(i<0||i>tableNameList.size()-1){
					ctrl = false; 
				}
				if(logType==0){
					int begin = 0;
					List<LogData> tempList = new ArrayList<LogData>();
					
					do {
						String cond = queryCond;
						if (lastRecordJson != null) {
							if (lastRecordJson.getInt("updown") == 0) {
								ctrl = i >= 0;
								if (tableName.equals(lastRecordJson.getString("tableName"))) {
									cond = queryCond + " and id<"+ lastRecordJson.getInt("id")+ " order by id desc";
								} else {
									cond = queryCond + " order by id desc";
								}
							} else {
								ctrl = i < tableNameList.size();
								if (tableName.equals(lastRecordJson.getString("tableName"))) {
									cond = queryCond + " and id>"+ lastRecordJson.getInt("id");
								}
							}
						}
						tempList = logCenterDAO.getLogData(tableName, cond,begin, 30000);
						begin += 30000;
						for (int j = 0; j < tempList.size(); j++) {
							LogData logData = tempList.get(j);
							if (logData.getProperties().contains("\n")) {
								logData.setProperties(logData.getProperties().replace("\n", ""));
							}
							JSONObject properties = JSONObject.fromObject(logData.getProperties());
							PropertiesParser parser = new PropertiesParser(jexlCond);
							//不能直接把logJson放进去，因为比方说条件logType=='ERROR'不规则，除非它是logJson.logType=='ERROR'才可以
							//多放了一些冗余数据，尽可能使输入错误时也可以正常运行
							parser.setVars("id", logData.getId());
							parser.setVars("user_id", logData.getLogId());
							parser.setVars("userId", logData.getLogId());
							parser.setVars("user_name", logData.getUserName());
							parser.setVars("username", logData.getUserName());
							parser.setVars("userName", logData.getUserName());
							parser.setVars("game_domain",logData.getGameDomain());
							parser.setVars("gameDomain",logData.getGameDomain());
							parser.setVars("create_time",logData.getCreateTime());
							parser.setVars("createTime",logData.getCreateTime());
							parser.setVars("reason", logData.getReason());
							parser.setVars("log_type", logData.getLogType());
							parser.setVars("logType", logData.getLogType());
							parser.setVars("extension", logData.getExtension());
							parser.setVars("properties", properties);
							
							parser.setVars("ip", properties.containsKey("ip")?properties.get("ip"):null);
							parser.setVars("gameServerDomain", properties.containsKey("gameServerDomain")?properties.get("gameServerDomain"):null);
							parser.setVars("areaId", properties.containsKey("areaId")?properties.get("areaId"):null);
							parser.setVars("roleId", properties.containsKey("roleId")?properties.get("roleId"):null);
							parser.setVars("bonusInfo", properties.containsKey("bonusInfo")?properties.get("bonusInfo"):null);
							parser.setVars("lineNumber", properties.containsKey("lineNumber")?properties.get("lineNumber"):null);
							parser.setVars("functionName", properties.containsKey("functionName")?properties.get("functionName"):null);
							parser.setVars("channelCode", properties.containsKey("channelCode")?properties.get("channelCode"):null);
							parser.setVars("errorCode", properties.containsKey("errorCode")?properties.get("errorCode"):null);
							try {
								if ((Boolean) parser.evaluate()) {
									JSONObject json = JSONObject.fromObject(logData);
									json.remove("createTime");
									json.accumulate("createTime", logData.getCreateTime().toString());
									json.remove("properties");
									json.accumulate("properties", logData.getProperties().toString() + " ");
									json.accumulate("tableId", tableNameList.indexOf(tableName));
									json.accumulate("tableName", tableName);
									jArray.add(json);
									if (jArray.size() > 9) {
										break;
									}
								}
							} catch (Exception e) {
								//如果表达式出错了，则直接返回空值
								//e.printStackTrace();
								throw new Exception(e.getMessage());
							}
						}
					} while (jArray.size() < 10&&tempList.size()!=0);
					if(jArray.size()>9){
						break;
					}
				}else{
					int begin = 0;
					List<TUrlTestData> tempList = new ArrayList<TUrlTestData>();
					
					do {
						String cond = queryCond;
						if (lastRecordJson != null) {
							if (lastRecordJson.getInt("updown") == 0) {
								ctrl = i >= 0;
								if (tableName.equals(lastRecordJson.getString("tableName"))) {
									cond = queryCond + " and id<"+ lastRecordJson.getInt("id")+ " order by id desc";
								} else {
									cond = queryCond + " order by id desc";
								}
							} else {
								ctrl = i < tableNameList.size();
								if (tableName.equals(lastRecordJson.getString("tableName"))) {
									cond = queryCond + " and id>"+ lastRecordJson.getInt("id");
								}
							}
						}
						tempList = indicatorDAO.getUrlTest(tableName,indicatorId, cond, begin, 30000);
						begin += 30000;
						for (int j = 0; j < tempList.size(); j++) {
							TUrlTestData urlTestData = tempList.get(j);
							if (urlTestData.getHeader()!=null&&urlTestData.getHeader().length()>0) {
								if (urlTestData.getHeader().contains("\n")) {
								urlTestData.setHeader(urlTestData.getHeader().replace("\n", ""));
								}
							}
							
							JSONObject header = JSONObject.fromObject(urlTestData.getHeader());
							PropertiesParser parser = new PropertiesParser(jexlCond);

							parser.setVars("id", urlTestData.getId());
							parser.setVars("indicatorId", urlTestData.getIndicatorId());
							parser.setVars("success", urlTestData.getSuccess());
							parser.setVars("statusCode",urlTestData.getStatusCode());
							parser.setVars("spendTime",urlTestData.getSpendTime());
							parser.setVars("createTime",urlTestData.getCreateTime());
							parser.setVars("header", header);
//							if(!header.has("Vary")){
//								int aa = 0;
//								aa++;
//							}
							parser.setVars("Vary", header.containsKey("Vary")?header.get("Vary"):null);
							parser.setVars("Transfer-Encoding", header.containsKey("Transfer-Encoding")?header.get("Transfer-Encoding"):null);
							parser.setVars("Date", header.containsKey("Date")?header.get("Date"):null);
							parser.setVars("Expires", header.containsKey("Expires")?header.get("Expires"):null);
							parser.setVars("Set-Cookie", header.containsKey("Set-Cookie")?header.get("Set-Cookie"):null);
							parser.setVars("Connection", header.containsKey("Connection")?header.get("Connection"):null);
							parser.setVars("Content-Type", header.containsKey("Content-Type")?header.get("Content-Type"):null);
							parser.setVars("Server", header.containsKey("Server")?header.get("Server"):null);
							parser.setVars("Cache-Control", header.containsKey("Cache-Control")?header.get("Cache-Control"):null);
							
							try {
								if ((Boolean) parser.evaluate()) {
									if (urlTestData.getHeader()!=null&&urlTestData.getHeader().length()>0) {
										if (urlTestData.getHeader().contains("\n")) {
											urlTestData.setHeader(urlTestData.getHeader().replace("\n", ""));
										}
									}
									JSONObject json = JSONObject.fromObject(urlTestData);
									json.remove("createTime");
									json.accumulate("createTime", urlTestData.getCreateTime().toString());
									if (urlTestData.getHeader()!=null&&urlTestData.getHeader().length()>0){
										json.remove("header");
										json.accumulate("header", urlTestData.getHeader().toString() + " ");
									}
									json.accumulate("tableId", tableNameList.indexOf(tableName));
									json.accumulate("tableName", tableName);
									jArray.add(json);
									//如果按的是上一页，则把数据倒序过来
									if (jArray.size() > 9) {
										break;
									}
								}
							} catch (Exception e) {
								throw new Exception(e.getMessage());
							}
						}
					} while (jArray.size() < 10&&tempList.size()!=0);
					if(jArray.size()>9){
						break;
					}
					}
			}
			//如果按的是上一页，则把数据倒序过来
			if(lastRecordJson!=null&&lastRecordJson.getInt("updown")==0){
				JSONArray tempArray = new JSONArray();
				for(int k=jArray.size()-1;k>=0;k--){
					tempArray.add(jArray.get(k));
				}
				jArray = tempArray;
			}
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate("logs", jArray);
			return jsonObject;
		}
	}
	/*
	 * 短信报警
	 */
	private void sendSms(String msg,String type,String group) throws Exception {
		
		logger.info("发送短信："+msg);
		
		String disbledalert = System.getProperty("monitor.disablealert");
		if(disbledalert!=null && disbledalert.equalsIgnoreCase("true")) return;
		
		String title = "监控系统提示";
		String encoding = "GBK";
		String mt = URLEncoder.encode(title,encoding);
		String c = URLEncoder.encode(msg, encoding);
		long a = System.currentTimeMillis()/1000;
		String s = type;
		String m = group;
		String url = "http://warn.io8.org/api/?c="+c+"&a="+a+"&s="+s+"&m="+m+"&mt="+mt;
		String response = MonitorUtil.sendRequest(url);
		if(!response.equals("msg:ok")) {
			throw new Exception("短信平台返回错误："+response);
		}
	}
	
	/**
	 * 生成图形
	 * @param id
	 * @param start
	 * @param end
	 * @param d
	 */
	public void chart(@Param("id")int id,@Param("start")String start,@Param("end")String end,@Param("d")String d) {
		HttpServletResponse response = inv.getResponse();
		
		try {
			Font songFont = new Font("宋体",Font.PLAIN,12);
			
			
			long now = System.currentTimeMillis();
			
			if(d==null||d.trim().length()==0) {
				d = MonitorUtil.ts2date(now);
				start = MonitorUtil.ts2hm(now-3600000);
				end = MonitorUtil.ts2hm(now);
				if(start.compareTo(end)>0) {
					start = "0000";
				}
			}
			
			TIndicator indicator = indicatorDAO.getById(id);
			
			int year = Integer.parseInt(d.substring(0,4));
			int month = Integer.parseInt(d.substring(4,6));
			int date = Integer.parseInt(d.substring(6,8));
			
			int start_hour = Integer.parseInt(start.substring(0,2));
			int start_minute = Integer.parseInt(start.substring(2,4));
			
			int end_hour = Integer.parseInt(end.substring(0,2));
			int end_minute = Integer.parseInt(end.substring(2,4));
			
			Timestamp start_ts = MonitorUtil.createTimestamp(year, month, date, start_hour, start_minute, 0);
			Timestamp end_ts = MonitorUtil.createTimestamp(year, month, date, end_hour, end_minute, 59);
			
			String tableName = "t_indicator_data_"+(year+MonitorUtil.getXX(month)+MonitorUtil.getXX(date));
			
			List<IndicatorData> dataList = new ArrayList<IndicatorData>();
			
			if(indicatorDAO.tableExists(tableName)) {
				dataList = indicatorDAO.getIndicatorData(start_ts, end_ts, id,tableName);
			}
			
			TimeSeries tsTotal = new TimeSeries("总数", Second.class);
			TimeSeries tsErrorCount = new TimeSeries("出错数", Second.class);
			TimeSeries tsErrorRate = new TimeSeries("出错率", Second.class);
			//修改的代码
			TimeSeries tsWarnValue = new TimeSeries("预警值", Second.class);
			TimeSeries tsAlertValue = new TimeSeries("报警值", Second.class);
			
			
			TimeSeriesCollection dsCount = new TimeSeriesCollection();
			TimeSeriesCollection dsRate = new TimeSeriesCollection();
			
			for(IndicatorData data:dataList) {
				Second sec = new Second(new Date(data.getCreateTime().getTime()));
				int normalCount = data.getNormalCount();
				int errorCount = data.getErrorCount();
				int totalCount = normalCount+errorCount;
				int errorRate = totalCount==0?0:(errorCount*100/totalCount);
				tsTotal.addOrUpdate(sec, new Integer(totalCount));
				tsErrorCount.addOrUpdate(sec, new Integer(errorCount));
				tsErrorRate.addOrUpdate(sec, new Integer(errorRate));
				//修改的代码
				tsWarnValue.addOrUpdate(sec, new Integer(indicator.getWarnValue()));
				tsAlertValue.addOrUpdate(sec, new Integer(indicator.getAlertValue()));
			}
			dsCount.addSeries(tsTotal);
			dsCount.addSeries(tsErrorCount);			
			dsRate.addSeries(tsErrorRate);
			//修改的代码
			dsCount.addSeries(tsWarnValue);
			dsCount.addSeries(tsAlertValue);
			
			JFreeChart chart = ChartFactory.createTimeSeriesChart("指标["+indicator.getIndicatorName()+"]趋势图", "", "", dsCount, true, false, false);
			chart.getTitle().setFont(songFont);
			chart.setAntiAlias(false);
			chart.setTextAntiAlias(false);
			chart.getLegend().setItemFont(songFont);
			XYPlot plot = (XYPlot)chart.getPlot();
			plot.getRangeAxis().setLowerBound(0);
			StandardXYItemRenderer renderer1 = new StandardXYItemRenderer();
			StandardXYItemRenderer renderer2 = new StandardXYItemRenderer();
			renderer1.setSeriesPaint(0, new Color(0,0,255));
			renderer1.setSeriesPaint(1, new Color(0,0,0));
			renderer2.setSeriesPaint(0, new Color(255,0,0));
			plot.setRenderer(0,renderer1);
			plot.setRenderer(1,renderer2);
			
			
			NumberAxis rateAxis = new NumberAxis("比率");
			rateAxis.setLabelFont(songFont);
			rateAxis.setAutoRangeIncludesZero(true);
			plot.setDataset(1, dsRate);
			plot.setRangeAxis(1, rateAxis);
			plot.setDomainAxis(1, plot.getDomainAxis());
			plot.getRangeAxis(1).setUpperBound(100);
			plot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);
			plot.mapDatasetToDomainAxis(1, 1);
			plot.mapDatasetToRangeAxis(1, 1);
			
			
			chart.setPadding(new RectangleInsets(10, 10, 10, 10));
			chart.setBorderVisible(true);
			
	        plot.setNoDataMessageFont(new Font("宋体",Font.PLAIN,12));
	        plot.setNoDataMessage("没有查询到数据，请更换查询条件");
			BufferedImage image = chart.createBufferedImage(800, 400);
			response.setContentType("image/png");
			ImageIO.write(image, "png", response.getOutputStream());
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			
			try {
				BufferedImage image = new BufferedImage(800, 100, BufferedImage.TYPE_INT_RGB);
				Graphics2D g = (Graphics2D)image.getGraphics();
				g.setColor(new Color(255,255,255));
				g.fillRect(0, 0, image.getWidth(), image.getHeight());
				g.setColor(new Color(255,0,0));
				g.setFont(new Font("宋体",Font.PLAIN,12));
				g.drawString("生成图表出错："+e.getMessage(), 5, 20);
				response.setContentType("image/png");
				ImageIO.write(image, "png", response.getOutputStream());
			} catch (IOException e1) {
				logger.error(e1.getMessage(),e1);
			}
			
		}
	}
	
	@Ajax
	public String urltest(TIndicator indicator) throws Exception {
		Map<String,Object> testResultMap = MonitorUtil.getUrltestResult(indicator);
		
		JsonMapContext context = new JsonMapContext(testResultMap);
		boolean result = MonitorUtil.parseCondition(indicator.getErrorCond(), context);
		
		StringBuffer sb = new StringBuffer();
		sb.append("◎URL:"+ MonitorUtil.parseUrl(indicator.getUrlPage())+"\r\n");
		sb.append("◎出错条件判断结果："+result+"\r\n");
		sb.append("-------------------------URL抓取结果--------------------------------\r\n");
		for(String key:testResultMap.keySet()) {
			if(!(key.equals("content")||key.equals("headers"))) {
				Object o = testResultMap.get(key);
				sb.append("【"+key+"】"+o+"\r\n");
			}
		}
		if(testResultMap.containsKey("headers")) {
			sb.append("【headers】"+testResultMap.get("headers")+"\r\n");
		}
		if(testResultMap.containsKey("content")) {
			sb.append("【content】\r\n"+testResultMap.get("content")+"\r\n");
		}
		
		return sb.toString();
	}
	
	/**
	 * 测试条件
	 * @param filterCond
	 * @param errorCond
	 * @param msg
	 * @return
	 */
	@Ajax
	public Map<String, Boolean> valid(@Param("filterCond")String filterCond,
		@Param("errorCond")String errorCond,@Param("msg")String msg) {
		
		try {
			Map<String,Boolean> result = new HashMap<String, Boolean>();
			
			boolean filterResult = MonitorUtil.parseCondition(filterCond, msg);
			boolean errorResult = false;
			if(filterResult) {
				errorResult = MonitorUtil.parseCondition(errorCond, msg);
			}
			
			result.put("filterResult", filterResult);
			result.put("errorResult", errorResult);
			
			return result;
		} catch (Exception e) {
			logger.error("测试表达式出错", e);
			throw new RuntimeException(e.getMessage());
		}
		
	}
	
	@Ajax
	public Object status(@Param("indicatorId")int indicatorId) {
		return indicatorDAO.getStatusById(indicatorId);
	}
	
}
