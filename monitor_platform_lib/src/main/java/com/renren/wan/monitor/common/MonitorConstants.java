package com.renren.wan.monitor.common;

public class MonitorConstants {
	public static final String SESSION_NAME = "monitorSession";
	
	public static final String EMAIL_REGEX = "^([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,3}$";
	public static final String MOBILE_REGEX = "^1[3|4|5|8]\\d{3,9}$";
	
	public static final String [] INDICATOR_LEVEL = {"正常","警告","出错"};
	
	
	public static final int INDICATOR_TYPE_LOG = 0;
	public static final int INDICATOR_TYPE_URL_TEST = 1;
	
	public static final int INDICATOR_ERROR_COUNT = 1;  //出错数
	public static final int INDICATOR_ERROR_RATE = 2;   //出错率
}
