package com.renren.wan.monitor.common;

import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.params.HttpParams;

public class MonitorHttpClient extends DefaultHttpClient {

	public MonitorHttpClient() {
	}

	public MonitorHttpClient(HttpParams params) {
		super(params);
	}

	public MonitorHttpClient(ClientConnectionManager conman, HttpParams params) {
		super(conman, params);
	}

	@Override
	protected HttpRequestRetryHandler createHttpRequestRetryHandler() {
		return new DefaultHttpRequestRetryHandler(0,false);
	}

}
