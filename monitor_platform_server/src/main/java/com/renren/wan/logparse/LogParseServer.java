package com.renren.wan.logparse;

import java.net.InetSocketAddress;

import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scribe.thrift.scribe.Processor;


public class LogParseServer {
	
	private static Logger logger = LoggerFactory.getLogger(LogParseServer.class);
	
	public static void main(String [] args) {
		try {
			if (args.length < 1) {
				throw new Exception("Parameters invalidate: java <port>");
			}
			
			
			int port = Integer.parseInt(args[0]);
			
			GlobalData.loadIndicatorStatusMap();
			IndicatorManager.getInstance();
			IndicatorDataManager.getInstance();
			MonitorDataManager.getInstance();
			LogDispatcher.getInstance();
			AlertManager.getInstance();
			UrlTestManager.getInstance();
			
			Processor processor = new Processor(new LogParseHandler());
			InetSocketAddress addr = new InetSocketAddress(port);
			TNonblockingServerTransport transport = new TNonblockingServerSocket(addr);
			TServer server = new THsHaServer(processor, transport);
			System.out.println("Starting Log Parse Server,listen on *:" + port);
			server.serve();
		} catch (Exception e) {
			System.out.println("System started error,Please check the exception information!");
			e.printStackTrace();
			logger.error(e.getMessage(),e);
			System.exit(0);
		}
	}
}
