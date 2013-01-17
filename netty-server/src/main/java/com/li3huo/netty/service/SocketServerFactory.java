/**
 * Create at Jan 16, 2013
 */
package com.li3huo.netty.service;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

/**
 * @author liyan
 * 
 */
public class SocketServerFactory {
	private Logger logger;
	private int port;
	private ChannelPipeline pipeline;
	
	private NioServerSocketChannelFactory server;
	private final AtomicLong accessCount = new AtomicLong();

	public AtomicLong getAccessCount() {
		return accessCount;
	}
	
	public Logger getLogger() {
		return logger;
	}

	public SocketServerFactory(int port, final ChannelPipeline pipeline) {

		this.port = port;
		this.pipeline = pipeline;

		/**
		 * init logger
		 */
		logger = Logger.getLogger("Server[" + this.port + "]");
		
	}
	
	public void bindAndStartServer() {
		/**
		 * Configure the server
		 * 
		 * Executor bossExecutor - boss thread pool
		 * 
		 * Executor workerExecutor - worker thread thread pool
		 * 
		 */
		server = new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool());

		ServerBootstrap bootstrap = new ServerBootstrap(server);
		
		logger.info("handlers in pipeline: "+pipeline);

		// Set up the pipeline factory.
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				return pipeline;
			}
		});

		// Bind and start to accept incoming connections.
		bootstrap.bind(new InetSocketAddress(port));
		
		logger.info("Server started at ["+port+"].");
	}
}
