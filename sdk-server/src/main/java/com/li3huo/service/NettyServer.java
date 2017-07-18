/**
 * 
 */
package com.li3huo.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.internal.SystemPropertyUtil;

/**
 * @author liyan
 *
 */
public class NettyServer {
	static final Logger logger = LogManager.getLogger(NettyServer.class.getName());

	private final static int BOSS_THREADS = 1;
	private final static int MAX_WORKER_THREADS = 16;
	private int port;

	public NettyServer(int port) {
		this.port = port;
	}

	/**
	 * <a href=
	 * "http://stackoverflow.com/questions/30367353/performance-tuning-for-netty-4-1-on-linux-machine">
	 * Performance tuning for Netty 4.1 on linux machine</a>
	 * 
	 * @return
	 */
	private int calculateThreadCount() {
		int threadCount;
		if ((threadCount = SystemPropertyUtil.getInt("io.netty.eventLoopThreads", 0)) > 0) {
			return threadCount;
		} else {
			threadCount = Runtime.getRuntime().availableProcessors() ;
			return threadCount > MAX_WORKER_THREADS ? MAX_WORKER_THREADS : threadCount;
		}
	}

	public void run() throws Exception {
		logger.warn("Starting Srv on " + port);
		// Configure the server
		EventLoopGroup bossGroup = new NioEventLoopGroup(BOSS_THREADS);
		EventLoopGroup workerGroup = new NioEventLoopGroup(calculateThreadCount());
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					// .handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new HttpRequestDecoder());
							// Uncomment the following line if you don't want to
							// handle HttpChunks.
							// ch.pipeline().addLast(new
							// HttpObjectAggregator(1048576));
							ch.pipeline().addLast(new HttpResponseEncoder());
							// Remove the following line if you don't want
							// automatic content compression.
							// ch.pipeline().addLast(new
							// HttpContentCompressor());
							ch.pipeline().addLast(new NettyHttpHandler());
						}
					}).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);

			// Bind and start to accept incoming connections.
			logger.debug("trying to listen on port "+port);
			ChannelFuture f = b.bind(port).sync();
			logger.warn("started and listen on " + f.channel().localAddress());

			// Wait until the server socket is closed.
			f.channel().closeFuture().sync();
			logger.debug("socket is closed");
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
			logger.warn("Srv Stoped.");
		}
	}

	public static void main(String[] args) throws Exception {
		int port;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		} else {
			port = 8000;
		}
		new NettyServer(port).run();
	}
}
