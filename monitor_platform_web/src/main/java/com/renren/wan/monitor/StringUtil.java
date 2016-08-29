package com.renren.wan.monitor;

/**
 * 字符串工具
 * @author Administrator
 *
 */
public class StringUtil {
	
	public static void checkNull(String fieldName,String str) {
		if(str==null || str.trim().length()==0) throw new CheckedException(fieldName+"不能为空"); 
	}
	
}
