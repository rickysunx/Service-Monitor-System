package com.renren.wan.logparse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.facebook.fb303.fb_status;

import scribe.thrift.LogEntry;
import scribe.thrift.ResultCode;
import scribe.thrift.scribe.Iface;

public class LogParseHandler implements Iface {
	private static Logger logger = LoggerFactory.getLogger(LogParseHandler.class);
	public final static String NAME = "scribe";
	public final static String VERSION = "0.0.1-r0";
	public final static String StatusDetails = "initial status";
	public final AtomicLong counter = new AtomicLong();
	public final ConcurrentHashMap<String, Long> counters = new ConcurrentHashMap<String, Long>();
	public long alive = System.currentTimeMillis()/1000;
	
	@Override
	public String getName() throws TException {
		return NAME;
	}

	@Override
	public String getVersion() throws TException {
		return VERSION;
	}

	@Override
	public fb_status getStatus() throws TException {
		return fb_status.ALIVE;
	}

	@Override
	public String getStatusDetails() throws TException {
		return StatusDetails;
	}

	@Override
	public Map<String, Long> getCounters() throws TException {
		return counters;
	}

	@Override
	public long getCounter(String key) throws TException {
		Long val = counters.get(key);
		if (val == null) {
			return 0;
		}
		return val.longValue();
	}

	@Override
	public void setOption(String key, String value) throws TException {

	}

	@Override
	public String getOption(String key) throws TException {
		return null;
	}

	@Override
	public Map<String, String> getOptions() throws TException {
		return null;
	}

	@Override
	public String getCpuProfile(int profileDurationInSec) throws TException {
		return "";
	}

	@Override
	public long aliveSince() throws TException {
		return alive;
	}

	@Override
	public void reinitialize() throws TException {

	}

	@Override
	public void shutdown() throws TException {
		
	}

	@Override
	public ResultCode Log(List<LogEntry> messages) throws TException {
		try {
			LogDispatcher.getInstance().addMessages(messages);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		}
		return ResultCode.OK;
	}

}
