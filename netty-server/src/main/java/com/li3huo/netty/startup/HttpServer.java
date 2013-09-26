/**
 * 
 */
package com.li3huo.netty.startup;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLEngine;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.ssl.SslHandler;

import com.li3huo.netty.service.ApplicationConfig;
import com.li3huo.netty.service.BusinessHandler;
import com.li3huo.netty.service.ConsoleHandler;
import com.li3huo.netty.service.proxy.HandlerSwitch;
import com.li3huo.netty.service.proxy.HttpIOTransferHandler;
import com.li3huo.netty.service.proxy.HttpOutboundWorker;
import com.li3huo.netty.service.ssl.SecureChatSslContextFactory;

/**
 * @author liyan
 * 
 */
public class HttpServer implements Server {

	private static Logger log = Logger.getLogger(HttpServer.class.getName());

	private static int businessPort, consolePort;

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

		ApplicationConfig.init();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.li3huo.netty.Server#start()
	 */
	public void start() {
		// startBusiness();
		/**
		 * suchao@exchange.hissage.org
		 * suchao/linfengfeiye-123
		 * proxy：172.27.237.12
		 */
		startProxy("172.27.236.207",443,true);
//		startProxy("li3huo.com", 80, false);
		startConsole();
	}

	private void startProxy(final String remoteHost, final int remotePort,
			final boolean ssl) {

		// Configure the bootstrap.
		Executor executor = Executors.newCachedThreadPool();

		/**
		 * create business server
		 */
		NioServerSocketChannelFactory server = new NioServerSocketChannelFactory(
				executor, executor);

		ServerBootstrap bootstrap = new ServerBootstrap(server);

		// Reuse address, powered by liumingfei.com
		bootstrap.setOption("reuseAddress", true);

		// Set up the event pipeline factory.
		final ClientSocketChannelFactory cf = new NioClientSocketChannelFactory(
				executor, executor);

		HttpOutboundWorker worker = new HttpOutboundWorker(cf, remoteHost,
				remotePort, ssl);

		final HttpIOTransferHandler transferHandler = new HttpIOTransferHandler(
				worker, null);

		// Set up the pipeline factory.
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();

				// add customised handler
				pipeline.addLast("inboundDecodeHandler", new HandlerSwitch(
						transferHandler));
				return pipeline;
			}
		});

		// Bind and start to accept incoming connections.
		bootstrap.bind(new InetSocketAddress(443));
		log.info("Business start at " + 443);
	}

	private void startBusiness() {

		/**
		 * create business server
		 */
		NioServerSocketChannelFactory server = new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool());

		ServerBootstrap bootstrap = new ServerBootstrap(server);

		// Reuse address, powered by liumingfei.com
		bootstrap.setOption("reuseAddress", true);

		// Set up the pipeline factory.
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();

				// Uncomment the following line if you want HTTPS
				SSLEngine engine = SecureChatSslContextFactory
						.getServerContext().createSSLEngine();
				engine.setUseClientMode(false);
				pipeline.addLast("ssl", new SslHandler(engine));

				pipeline.addLast("decoder", new HttpRequestDecoder());
				pipeline.addLast("encoder", new HttpResponseEncoder());

				// add customised handler
				pipeline.addLast("handler", new BusinessHandler(businessPort));
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

		ServerBootstrap bootstrap = new ServerBootstrap(server);

		// Set up the pipeline factory.
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();

				pipeline.addLast("decoder", new HttpRequestDecoder());
				pipeline.addLast("encoder", new HttpResponseEncoder());

				// add customised handler
				pipeline.addLast("handler", new ConsoleHandler(consolePort));
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
		System.out.println("Show Status");
	}
}
