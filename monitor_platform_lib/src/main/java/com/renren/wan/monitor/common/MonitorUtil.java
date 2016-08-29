package com.renren.wan.monitor.common;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.renren.wan.monitor.entities.TIndicator;


/**
 * 监控系统工具类
 * @author rui.sun1
 *
 */
public class MonitorUtil {
	
	private static Logger logger = LoggerFactory.getLogger(MonitorUtil.class);
	
	public static final char[] passWordChars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	
	/**
	 * 获取随机密码
	 * @param length
	 * @return
	 */
	public static String getRandomPassWord(int length) {
		StringBuffer sb = new StringBuffer(length);
		
		Random r = new Random(System.currentTimeMillis());
		int charCount = passWordChars.length;
		for(int i=0;i<length;i++) {
			sb.append(passWordChars[r.nextInt(charCount)]);
		}
		
		return sb.toString();
	}
	
	public static String getIndicatorStatusHtml(int status) {
		String s = "";
		if(status==0) {
			s = "<span style='color:#fff;border:1px solid #000;background-color:#008000;padding:2px;'>正常</span>";
		} else if(status==1) {
			s = "<span style='color:#000;border:1px solid #000;background-color:#eeee00;padding:2px;'>警告</span>";
		} else if(status==2) {
			s = "<span style='color:#fff;border:1px solid #000;background-color:#ee0000;padding:2px;'>出错</span>";
		}
 		
		return s;
	}
	
	public static String getXX(int n) {
		return (n<10)?("0"+n):(""+n);
	}
	
	/**
	 * 获取日期
	 * @param ts 毫秒为单位
	 * @return
	 */
	public static String ts2date(long ts) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(ts);
		String s = c.get(Calendar.YEAR)+getXX(c.get(Calendar.MONTH)+1)+getXX(c.get(Calendar.DATE));
		return s;
	}
	
	public static String ts2datetime(long ts) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(ts);
		String s = c.get(Calendar.YEAR)+"-"+getXX(c.get(Calendar.MONTH)+1)+"-"+getXX(c.get(Calendar.DATE))+" "+
			getXX(c.get(Calendar.HOUR_OF_DAY))+":"+
			getXX(c.get(Calendar.MINUTE))+":"+
			getXX(c.get(Calendar.SECOND));
		return s;
	}
	
	/**
	 * ts-> 0910
	 * @param ts
	 * @return
	 */
	public static String ts2hm(long ts) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(ts);
		String s = getXX(c.get(Calendar.HOUR_OF_DAY))+""+
			getXX(c.get(Calendar.MINUTE));
		return s;
	}
	
	/**
	 * 获取时间
	 * @param ts 毫秒
	 * @return
	 */
	public static String ts2time(long ts) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(ts);
		String s = getXX(c.get(Calendar.HOUR_OF_DAY))+":"+
			getXX(c.get(Calendar.MINUTE))+":"+
			getXX(c.get(Calendar.SECOND));
		return s;
	}
	
	private static ThreadLocal<JexlEngine> jexlThreadLocal = new ThreadLocal<JexlEngine>();
	
	public static boolean parseCondition(String expression,JexlContext context) {
		if(expression==null || expression.trim().length()==0) return false;
		JexlEngine jexl = jexlThreadLocal.get();
		if(jexl==null) {
			jexl = new JexlEngine();
			jexlThreadLocal.set(jexl);
		}
		Expression exp = jexl.createExpression(expression);
		Object o = exp.evaluate(context);
		if(o!=null && o instanceof Boolean && ((Boolean)o).booleanValue()) {
			return true;
		}
		return false;
	}
	
	public static boolean parseCondition(String expression,String msg) {
		JsonMapContext context = new JsonMapContext(msg);
		return parseCondition(expression, context);
	}
	
	public static String sendRequest(String url) throws Exception {
		URL theUrl = null;
		HttpURLConnection conn = null;
		try {
			theUrl = new URL(url);
			conn = (HttpURLConnection)theUrl.openConnection();
			conn.setUseCaches(false);
			conn.setConnectTimeout(10000);
			conn.connect();
			
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte [] buff = new byte[512];
			int len;
			InputStream in = conn.getInputStream();
			while( (len=in.read(buff))>0 ) {
				bout.write(buff,0,len);
			}
			byte [] bcontent = bout.toByteArray();
			String response = new String(bcontent,"UTF-8");
			return response;
		} finally {
			try {
				if(conn!=null) conn.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static long getDayStartTs(long ts) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(ts);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		return c.getTimeInMillis();
	}
	
	public static long getDayEndTs(long ts) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(ts);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		
		return c.getTimeInMillis();
	}
	
	
	public static String md5(String str)  {
		
		return DigestUtils.md5Hex(str);
		
//		String s = str;
//		if (s == null) {
//			return "";
//		} else {
//			String value = "";
//			MessageDigest md5 = null;
//			try {
//				md5 = MessageDigest.getInstance("MD5");
//			} catch (NoSuchAlgorithmException ex) {
//				logger.error(ex.getMessage(), ex);
//			}
//			try {
//				byte [] bytes = md5.digest(s.getBytes("utf-8"));
//				for (int i = 0; i < bytes.length; i++) {
//					String hh = Integer.toHexString(bytes[i]>=0?bytes[i]:(256+bytes[i])).toLowerCase();
//					value+=(hh.length()==1?"0"+hh:hh);
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			return value;
//		}
	}
	
	public static Timestamp createTimestamp(int year,int month,int date,int hour,int minute,int second) {
		Calendar c = Calendar.getInstance();
		c.set(year, month-1, date, hour, minute, second);
		c.set(Calendar.MILLISECOND, 0);
		return new Timestamp(c.getTimeInMillis());
	}
	//依据输入的时间返回时间所在的毫秒数
	public static long getMiliTime(String time) {
		SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date = formate.parse(time);
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			long miliTime = c.getTimeInMillis();
			return miliTime;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static boolean int2bool(Integer n) {
		return (n!=null) && (n>0);
	}
	
	public static int bool2int(Boolean b) {
		return (b!=null && b)?1:0;
	}
	
	public static Map<String, Object> getUrltestResult(TIndicator indicator) throws Exception {
		HttpClient http = null;
		Map<String,Object> resultMap = new LinkedHashMap<String, Object>();
		long time_start = System.currentTimeMillis();
		try {
			String method = indicator.getUrlMethod();
			String url = MonitorUtil.parseUrl(indicator.getUrlPage());
			int timeout = indicator.getUrlTimeout();
			Integer indicatorId = indicator.getIndicatorId();
			if(url==null || url.trim().length()==0) throw new Exception("url未指定");
			if(method==null) throw new Exception("method未指定");
			
			http = new MonitorHttpClient();
			resultMap = new LinkedHashMap<String, Object>();
			
			resultMap.put("indicatorId", indicatorId);
			resultMap.put("statusCode", -1);
			resultMap.put("content", "");
			
			http.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);
			http.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);
			//http.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
			
			HttpResponse response = null;
			if(method.equals("GET")) {
				HttpGet get = new HttpGet(url);
				get.setHeader("Connection", "close");
				response = http.execute(get);
			} else if(method.equals("POST")) {
				HttpPost post = new HttpPost(url);
				post.setHeader("Connection","close");
				String postData = indicator.getUrlPostData();
				if(postData!=null && postData.trim().length()>0) {
					StringEntity postEntity = new StringEntity(postData,"UTF-8");
					post.setEntity(postEntity);
				}
				response = http.execute(post);
			}
			
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			resultMap.put("statusCode", statusCode);
			if(statusCode==200) {
				resultMap.put("success", true);
			} else {
				resultMap.put("success", false);
			}
			
			Header[] headers = response.getAllHeaders();
			if(headers!=null && headers.length>0) {
				Map<String, String> headerMap = new HashMap<String, String>();
				for(Header header:headers) {
					headerMap.put(header.getName(), header.getValue());
				}
				resultMap.put("headers", headerMap);
			}
			
			HttpEntity entity = response.getEntity();
			if(entity!=null) {
				byte [] buff = new byte[512];
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				int len;
				InputStream in = null;
				try {
					in = entity.getContent();
					while( (len=in.read(buff))>0) {
						bout.write(buff, 0, len);
					}
				} finally {
					in.close();
				}
				
				byte [] byteContent = bout.toByteArray();
				
				String charset = indicator.getUrlCharset();
				if(charset==null || charset.trim().equalsIgnoreCase("auto")) {
					charset = "UTF-8";
					Header contentTypeHeader = response.getFirstHeader("Content-Type");
					if(contentTypeHeader!=null) {
						String contentTypeValue = contentTypeHeader.getValue();
						int index = contentTypeValue.indexOf("charset=");
						if(index>=0) {
							int start = index+"charset=".length();
							charset = contentTypeValue.substring(start, contentTypeValue.length());
						}
					}
				}
				
				String content = new String(byteContent,charset);
				
				resultMap.put("content", content);
			}
			
			long time_end = System.currentTimeMillis();
			resultMap.put("spendTime", (int)(time_end-time_start));
			resultMap.put("timeout", false);
		} catch (InterruptedIOException e) {
			logger.error(e.getMessage(), e.getMessage());			
			long time_end = System.currentTimeMillis();
			resultMap.put("success", false);
			resultMap.put("errorMsg", e.getMessage());
			resultMap.put("spendTime", (int)(time_end-time_start));
			resultMap.put("timeout", true);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			long time_end = System.currentTimeMillis();
			resultMap.put("success", false);
			resultMap.put("errorMsg", e.getMessage());
			resultMap.put("spendTime", (int)(time_end-time_start));
		} finally {
			try {if(http!=null) http.getConnectionManager().shutdown();} catch (Exception e) {}
		}
		
		return resultMap;
	}
	
	
	/**
	 * 解析动态URL
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String parseUrl(String url) throws Exception {
		String newURL = url;
		newURL = newURL.replaceAll("\\$\\{timestamp\\}", ""+(System.currentTimeMillis()/1000));
		
		String [] funcs = new String [] {"md5_param","md5_param_with_name"};
		Map<String, String> paramMap = null;
		
		
		for(String f:funcs) {
			String f_prefix = "${"+f+"(";
			for(int start = 0;;) {
				int f_start = newURL.indexOf(f_prefix,start);
				if(f_start==-1) break;
				if(paramMap==null) {
					paramMap = parseUrlParam(newURL);
				}
				int param_start = f_start + f_prefix.length();
				int param_end = newURL.indexOf(")",param_start);
				
				if(param_end==-1) {
					throw new Exception("expect ')'");
				}
				
				String paramString = newURL.substring(param_start,param_end);
				
				String [] paramKeys = paramString.split(",");
				StringBuffer sb = new StringBuffer();
				for(String key:paramKeys) {
					if(key==null || key.trim().length()==0) continue;
					key = key.trim();
					if(key.startsWith("\"")) {
						if(key.endsWith("\"")) {
							sb.append(key.substring(1, key.length()-2));
						} else {
							throw new Exception("expect \"");
						}
					} else {
						if(f.equals("md5_param")) {
							sb.append(paramMap.get(key)==null?"":paramMap.get(key));
						} else if(f.equals("md5_param_with_name")) {
							sb.append(key+"="+(paramMap.get(key)==null?"":paramMap.get(key)));
						}
					}
				}
				int f_end = newURL.indexOf("}",start);
				newURL = (newURL.substring(0, f_start)+md5(sb.toString())+newURL.substring(f_end+1, newURL.length()));
				start = f_start+sb.length();
			}
		}
		
		return newURL;
	}
	
	private static Map<String,String> parseUrlParam(String url) throws Exception {
		Map<String,String> paramMap = new HashMap<String, String>();
		int indexQuestion = url.indexOf("?");
		if(indexQuestion>=0) {
			String queryString = url.substring(indexQuestion+1,url.length()).trim();
			String [] paramArray = queryString.split("&");
			for(String param:paramArray) {
				String [] arr = param.split("=");
				if(arr.length==2) {
					String key = arr[0];
					String value = arr[1];
					paramMap.put(key, URLDecoder.decode(value, "UTF-8"));
				}
			}
		}
		
		return paramMap;
	}
	
}
