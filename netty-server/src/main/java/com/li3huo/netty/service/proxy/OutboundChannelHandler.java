/**
 * Create at Sep 22, 2013
 * @author liyan
 * 
 */
package com.li3huo.netty.service.proxy;

import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * @author liyan
 * 
 */
public class OutboundChannelHandler extends SimpleChannelUpstreamHandler {

	private Channel inboundChannel;
	private Object trafficLock;

	OutboundChannelHandler(Channel inboundChannel, Object trafficLock) {
		this.inboundChannel = inboundChannel;
		this.trafficLock = trafficLock;
	}

	public void putRequest(ChannelBuffer msg) {
		System.out
				.println(">>> "
						+ msg.toString(0, msg.readableBytes(),
								Charset.defaultCharset()));

		// Decode SSL and GZip
		// Encode to HTTP and check request info
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, final MessageEvent e)
			throws Exception {
		ChannelBuffer msg = (ChannelBuffer) e.getMessage();
		// System.out.println("<<< "+msg);
		System.out
				.println("<<< "
						+ msg.toString(0, msg.readableBytes(),
								Charset.defaultCharset()));
		//decode msg and check
		
		synchronized (trafficLock) {
			inboundChannel.write(msg);
			// If inboundChannel is saturated, do not read until notified in
			// HexDumpProxyInboundHandler.channelInterestChanged().
			if (!inboundChannel.isWritable()) {
				e.getChannel().setReadable(false);
			}
		}
	}

	@Override
	public void channelInterestChanged(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {
		// If outboundChannel is not saturated anymore, continue accepting
		// the incoming traffic from the inboundChannel.
		synchronized (trafficLock) {
			if (e.getChannel().isWritable()) {
				inboundChannel.setReadable(true);
			}
		}
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		closeOnFlush(inboundChannel);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		e.getCause().printStackTrace();
		closeOnFlush(e.getChannel());
	}

	/**
	 * Closes the specified channel after all queued write requests are flushed.
	 */
	void closeOnFlush(Channel ch) {
		if (ch.isConnected()) {
			ch.write(ChannelBuffers.EMPTY_BUFFER).addListener(
					ChannelFutureListener.CLOSE);
		}
	}
}
