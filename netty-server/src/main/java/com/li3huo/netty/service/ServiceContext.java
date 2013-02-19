/**
 * Create at Jan 17, 2013
 */
package com.li3huo.netty.service;

import org.apache.log4j.Logger;

import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.li3huo.netty.service.snapshot.SnapshotService;

/**
 * @author liyan
 * 
 */
public class ServiceContext {

	private Logger logger = Logger.getLogger(ServiceContext.class.getName());
	private static SnapshotService snapshot = new SnapshotService();

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

	public SnapshotService getSnapshotService() {
		return snapshot;
	}

	public Logger getLogger() {
		return logger;
	}
}
