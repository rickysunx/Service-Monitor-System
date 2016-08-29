package com.renren.wan.monitor.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;

import net.paoding.rose.web.InvocationLocal;
import net.paoding.rose.web.annotation.Param;
import net.paoding.rose.web.annotation.Path;
import net.paoding.rose.web.annotation.rest.Get;
import net.paoding.rose.web.annotation.rest.Post;

import com.renren.wan.monitor.CheckedException;
import com.renren.wan.monitor.WebUtil;
import com.renren.wan.monitor.dao.UserDAO;
import com.renren.wan.monitor.entities.TUser;

@Path("")
public class SsoController {
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private InvocationLocal inv;
	
	private String AppId="189";
	private String AppKey="162d380c947ca5bcbc4d650c1312f5d6";
	
	@Get({"ssoLogin"})
	public String ssoLogin(){
		return "r:http://sso.data.io8.org/?mod=api&act=main&app="+ AppId;
	}
	
	@Get("sso")
	@Post("sso")
	public String api(@Param("token")String token)
	{
		if(token!=null&&token.length()==40)
		{
			try {				
				String hash_send = md5(token+AppKey);
				String url = "http://sso.data.io8.org/?mod=api&act=auth";
				String param = "token="+token+"&hash="+hash_send;
				String result = this.sendPost(url, param);
				String par[] = result.split("&");
				String success ="";
				String username ="";
				String hash_back = "";
				for(int i=0;i<par.length;i++){
					String key = par[i].split("=")[0];
					String value = par[i].split("=").length>1? par[i].split("=")[1]:"";
					if("success".equals(key)){success = value;}
					if("hash".equals(key)){hash_back = value;}
					if("username".equals(key)){username = URLDecoder.decode(value);}
				}
				String 	s = md5((username+AppKey));
				username = username.substring(0,username.indexOf("@"));
				if(success.equals("1")&&hash_back.equals(s))
				{
					TUser user = userDAO.findByLogName(username);
					if(user==null)return "@<script>alert(\"您的用户名:"+username+"在此系统没有开通用户,\\n请联系管理员\");window.location.href=\"http://sso.data.io8.org\";</script>";
					WebUtil.setLoginUser(inv, user);
					return "r:/";
				}				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "r:http://sso.data.io8.org";
	}
	/** 
	* 向指定URL发送POST方法的请求 
	* @param url 发送请求的URL 
	* @param param 请求参数，请求参数应该是name1=value1&name2=value2的形式。 
	* @return URL所代表远程资源的响应 
	*/ 
	private String sendPost(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result +=line;
			}
		} catch (Exception e) {
			System.out.println("发送POST请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}
	private String md5(String message) {
		MessageDigest messageDigest = null;
		
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			
			messageDigest.reset();
			
			messageDigest.update(message.getBytes("UTF-8"));
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		byte [] encryptByte = messageDigest.digest();
		
		StringBuffer md5Str = new StringBuffer();
		
		for (int i = 0; i < encryptByte.length; i++) {
			if (Integer.toHexString(0xFF & encryptByte[i]).length() == 1) {
				md5Str.append("0").append(Integer.toHexString(0xFF & encryptByte[i]));
			} else {
				md5Str.append(Integer.toHexString(0xFF & encryptByte[i]));
			}
		}
		
		return md5Str.toString();
	}
}
