/**
 * Create at Sep 23, 2013
 * @author liyan
 * 
 */
package com.li3huo.netty.service.proxy;

import static org.jboss.netty.channel.Channels.pipeline;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLEngine;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.ssl.SslHandler;

import com.li3huo.netty.service.ssl.SecureChatSslContextFactory;

public class HttpOutboundWorker {

	private final ClientSocketChannelFactory cf;
	private final String remoteHost;
	private final int remotePort;
	private boolean ssl;

	public HttpOutboundWorker(ClientSocketChannelFactory cf, String remoteHost,
			int remotePort, boolean ssl) {
		this.cf = cf;
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
		this.ssl = ssl;
	}

	public HttpOutboundWorker getNewWorker() {
		return new HttpOutboundWorker(cf, remoteHost, remotePort, ssl);
	}

	public void run(final HttpRequest request, final Channel inboundChannel) {

		// Configure the client.
		final ClientBootstrap bootstrap = new ClientBootstrap(cf);

		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {

			public ChannelPipeline getPipeline() throws Exception {
				// Create a default pipeline implementation.
				ChannelPipeline pipeline = pipeline();

				// Enable HTTPS if necessary.
				if (ssl) {
					SSLEngine engine = SecureChatSslContextFactory
							.getClientContext().createSSLEngine();
					engine.setUseClientMode(true);

					pipeline.addLast("ssl", new SslHandler(engine));
				}

				pipeline.addLast("codec", new HttpClientCodec());

				// Remove the following line if you don't want automatic content
				// decompression.
				pipeline.addLast("inflater", new HttpContentDecompressor());

				// Uncomment the following line if you don't want to handle
				// HttpChunks.
				pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));

				pipeline.addLast("handler", new HttpOutboundClientHandler(
						inboundChannel));
				return pipeline;
			}
		});

		// Start the connection attempt.
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(
				remoteHost, remotePort));

		// // Wait until the connection attempt succeeds or fails.
		// Channel channel = future.awaitUninterruptibly().getChannel();
		// if (!future.isSuccess()) {
		// future.getCause().printStackTrace();
		// bootstrap.releaseExternalResources();
		// return;
		// }

		// // Send the HTTP request.
		// channel.write(request);
		// // Wait for the server to close the connection.
		// channel.getCloseFuture().awaitUninterruptibly();
		// // Shut down executor threads to exit.
		// bootstrap.releaseExternalResources();

		final Channel outboundChannel = future.getChannel();
		future.addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture future)
					throws Exception {
				if (future.isSuccess()) {
					// Connection attempt succeeded:

					// Send the HTTP request.
					request.setHeader(HttpHeaders.Names.HOST, remoteHost);
					// request.setHeader(HttpHeaders.Names.ACCEPT_ENCODING,
					// HttpHeaders.Values.NONE);

					ChannelBufferUtils.logHttpRequest(request);

					outboundChannel.write(request);
				} else {
					// Close the connection if the connection attempt has
					// failed.
					outboundChannel.close();
					bootstrap.releaseExternalResources();
				}
			}
		});

	}

	/**
	 * Closes the specified channel after all queued write requests are flushed.
	 */
	static void closeOnFlush(Channel ch) {
		if (ch.isConnected()) {
			ch.write(ChannelBuffers.EMPTY_BUFFER).addListener(
					ChannelFutureListener.CLOSE);
		}
	}

	public static void main(String[] args) {

		/**
		 * https://172.27.236.207/owa
		 */
		String remoteHost = "172.27.236.207";
		int remotePort = 443;
		boolean ssl = true;

		// Configure the bootstrap.
		Executor executor = Executors.newCachedThreadPool();

		// Set up the event pipeline factory.
		final ClientSocketChannelFactory cf = new NioClientSocketChannelFactory(
				executor, executor);
		HttpOutboundWorker worker = new HttpOutboundWorker(cf, remoteHost,
				remotePort, ssl);
		// Prepare the HTTP request.
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1,
				HttpMethod.GET, "/owa");
		request.setHeader(HttpHeaders.Names.HOST, remoteHost);
		request.setHeader(HttpHeaders.Names.CONNECTION,
				HttpHeaders.Values.CLOSE);
		request.setHeader(HttpHeaders.Names.ACCEPT_ENCODING,
				HttpHeaders.Values.GZIP);

		worker.run(request, null);
	}

}
