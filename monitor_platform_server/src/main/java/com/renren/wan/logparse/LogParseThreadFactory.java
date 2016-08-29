package com.renren.wan.logparse;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class LogParseThreadFactory implements ThreadFactory {

	protected String poolName;
	protected AtomicInteger threadCount;
	
	public LogParseThreadFactory(String poolName) {
		this.poolName = poolName;
		this.threadCount = new AtomicInteger(0);
	}
	
	@Override
	public Thread newThread(Runnable r) {
		int index = threadCount.incrementAndGet()-1;
		String name = "ExecPool-"+poolName+"-"+index;
		
		Thread t = new Thread(r,name);
		if(t.isDaemon()) t.setDaemon(false);
		if(t.getPriority()!=Thread.NORM_PRIORITY) t.setPriority(Thread.NORM_PRIORITY);
		return t;
	}

}
