/**
 * 
 */
package com.li3huo.netty;

import static org.jboss.netty.channel.Channels.pipeline;

import java.util.logging.Logger;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

import com.li3huo.netty.service.ConsoleHandler;
import com.li3huo.netty.service.HttpRequestHandler;
import com.li3huo.netty.service.SocketServerFactory;

/**
 * @author liyan
 * 
 */
public class DemoServer {

	private static Logger log = Logger.getLogger(DemoServer.class.getName());

	private static int businessPort, consolePort;

	private static SocketServerFactory businessServer, consoleServer;

	public static void init() {
		businessPort = 8080;
		consolePort = 8005;
	}

	public static void start() {
		startBusiness();
		startConsole();
	}

	private static void startBusiness() {
		init();
		/**
		 * create ChannelPipeline for business server
		 */
		ChannelPipeline pipeline = pipeline();
		businessServer = new SocketServerFactory(businessPort, pipeline);

		pipeline.addLast("decoder", new HttpRequestDecoder());
		pipeline.addLast("encoder", new HttpResponseEncoder());

		// add customised handler
		pipeline.addLast("handler", new HttpRequestHandler(businessServer));

		businessServer.bindAndStartServer();

	}

	private static void startConsole() {
		ChannelPipeline pipeline = pipeline();
		consoleServer = new SocketServerFactory(consolePort, pipeline);

		pipeline.addLast("decoder", new HttpRequestDecoder());
		pipeline.addLast("encoder", new HttpResponseEncoder());

		// add customised handler
		pipeline.addLast("handler", new ConsoleHandler(businessServer, consoleServer));

		consoleServer.bindAndStartServer();
	}

	public static void stop() {

	}

	public static void status() {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			String command = "start";
			if (args.length > 0) {
				command = args[args.length - 1];
			}

			if (command.equals("start")) {
				log.info("Starting server...");
				// init reflaction, log4j etc...
				// RNMService.init();
				try {
					start();
				} catch (Exception e) {
					e.printStackTrace();
					log.warning("error: " + e.getMessage());
					log.warning("exit program. pls check and restart again.");
					System.exit(0);
				}
			} else if (command.equals("stop")) {
				log.info("Stopping...");
				stop();
			} else if (command.equals("status")) {
				log.info("Stopping...");
				status();
			} else {
				log.warning("Bootstrap: command \"" + command
						+ "\" does not exist.");
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

}
