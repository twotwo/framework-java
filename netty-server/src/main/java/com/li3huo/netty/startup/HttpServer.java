/**
 * 
 */
package com.li3huo.netty.startup;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

import com.li3huo.netty.service.ConsoleHandler;
import com.li3huo.netty.service.HttpRequestHandler;
import com.li3huo.netty.service.ServiceContext;

/**
 * @author liyan
 * 
 */
public class HttpServer implements Server {

	private static Logger log = Logger.getLogger(HttpServer.class.getName());

	private static int businessPort, consolePort;

	private static ServiceContext context = new ServiceContext();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.li3huo.netty.Server#init()
	 */
	public void init() {

		String envPort = System.getenv("PORT_BUSINESS");
		if (null != envPort) {
			businessPort = Integer.parseInt(envPort);
		}
		if (businessPort <= 0) {
			businessPort = 8080;
		}
		consolePort = 8005;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.li3huo.netty.Server#start()
	 */
	public void start() {
		startBusiness();
		startConsole();
	}

	private void startBusiness() {
		init();

		/**
		 * create business server
		 */
		NioServerSocketChannelFactory server = new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool());

		context.setBusinessServer(businessPort, server);

		ServerBootstrap bootstrap = new ServerBootstrap(server);

		// Reuse address, powered by liumingfei.com
		bootstrap.setOption("reuseAddress", true);

		// Set up the pipeline factory.
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();

				pipeline.addLast("decoder", new HttpRequestDecoder());
				pipeline.addLast("encoder", new HttpResponseEncoder());

				// add customised handler
				pipeline.addLast("handler", new HttpRequestHandler(
						businessPort, context));
				return pipeline;
			}
		});

		// Bind and start to accept incoming connections.
		bootstrap.bind(new InetSocketAddress(businessPort));
		log.info("Business start at " + businessPort);
	}

	private void startConsole() {
		/**
		 * create business server
		 */
		NioServerSocketChannelFactory server = new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool());

		context.setConsoleServer(consolePort, server);

		ServerBootstrap bootstrap = new ServerBootstrap(server);

		// Set up the pipeline factory.
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();

				pipeline.addLast("decoder", new HttpRequestDecoder());
				pipeline.addLast("encoder", new HttpResponseEncoder());

				// add customised handler
				pipeline.addLast("handler", new ConsoleHandler(consolePort,
						context));
				return pipeline;
			}
		});

		// Bind and start to accept incoming connections.
		bootstrap.bind(new InetSocketAddress(consolePort));
		log.info("Console start at " + consolePort);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.li3huo.netty.Server#stop()
	 */
	public void stop() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.li3huo.netty.Server#status()
	 */
	public void status() {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Server daemon = new HttpServer();

		try {
			String command = "start";
			if (args.length > 0) {
				command = args[args.length - 1];
			}

			if (command.equals("start")) {
				log.info("Starting server...");
				// init reflaction, log4j etc...
				// ApplicationConfig.init();
				try {
					daemon.start();
				} catch (Exception e) {
					e.printStackTrace();
					log.fatal(e.getMessage());
					log.fatal("exit program. pls check and restart again.");
					System.exit(0);
				}
			} else if (command.equals("stop")) {
				log.info("Stopping...");
				daemon.stop();
			} else if (command.equals("status")) {
				log.info("Status...");
				daemon.status();
			} else {
				log.warn("Bootstrap: command \"" + command
						+ "\" does not exist.");
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

}
