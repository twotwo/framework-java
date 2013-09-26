/**
 * Create at Sep 23, 2013
 * @author liyan
 * 
 */
package com.li3huo.netty.service.proxy;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpResponse;

public class HttpOutboundClientHandler extends SimpleChannelUpstreamHandler {

	static Logger logger = Logger.getLogger(HttpOutboundClientHandler.class);

	final private Channel inboundChannel;

	public HttpOutboundClientHandler(Channel inboundChannel) {
		this.inboundChannel = inboundChannel;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		HttpResponse response = (HttpResponse) e.getMessage();
		
		ChannelBufferUtils.logHttpResponse(response);
		
		inboundChannel.write(response);
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {
		closeOnFlush(e.getChannel());
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		closeOnFlush(e.getChannel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		// e.getCause().printStackTrace();
		if (e.getCause() instanceof java.io.IOException) {
			// Ignore reset by peer.
			if (!"Connection reset by peer".equals(e
					.getCause().getMessage()))
				logger.error(e);
		} else {
			logger.error(e);
		}
		closeOnFlush(e.getChannel());
	}

	void closeOnFlush(Channel ch) {
		if (ch.isConnected()) {
			ch.write(ChannelBuffers.EMPTY_BUFFER).addListener(
					ChannelFutureListener.CLOSE);
		}
	}

}
