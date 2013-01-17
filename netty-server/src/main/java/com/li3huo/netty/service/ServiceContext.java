/**
 * Create at Jan 17, 2013
 */
package com.li3huo.netty.service;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

/**
 * @author liyan
 * 
 */
public class ServiceContext {
	
	private Logger logger = Logger.getLogger(ServiceContext.class.getName());
	private final AtomicLong accessCount = new AtomicLong();

	int businessPort, consolePort;
	NioServerSocketChannelFactory businessServer, consoleServer;

	public void setBusinessServer(int businessPort,
			NioServerSocketChannelFactory businessServer) {
		this.businessPort = businessPort;
		this.businessServer = businessServer;
	}
	
	public void setConsoleServer(int consolePort,
			NioServerSocketChannelFactory consoleServer) {
		this.consolePort = consolePort;
		this.consoleServer = consoleServer;
	}
	
	public AtomicLong getAccessCount() {
		return accessCount;
	}
	
	public Logger getLogger() {
		return logger;
	}
}
