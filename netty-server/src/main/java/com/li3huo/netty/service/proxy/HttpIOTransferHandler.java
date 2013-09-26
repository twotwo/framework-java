/**
 * Create at Sep 23, 2013
 * @author liyan
 * 
 */
package com.li3huo.netty.service.proxy;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.util.CharsetUtil;

import com.li3huo.business.MailLogic;

public class HttpIOTransferHandler extends SimpleChannelUpstreamHandler {

	static Logger logger = Logger.getLogger(HttpIOTransferHandler.class);

	private HttpOutboundWorker worker;

	public HttpIOTransferHandler(HttpOutboundWorker worker,
			Channel inboundChannel) {

		this.worker = worker;
		this.inboundChannel = inboundChannel;
	}

	Channel inboundChannel, outboundChannel;

	public HttpIOTransferHandler getNewHandler(Channel inboundChannel) {

		return new HttpIOTransferHandler(worker.getNewWorker(), inboundChannel);
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		HttpRequest request = (HttpRequest) e.getMessage();

		/*
		 * 根据request进行访问控制
		 */
		if (!MailLogic.authenticate(request)) {
			HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
			response.setContent(ChannelBuffers.copiedBuffer(
					"account disabled.", CharsetUtil.UTF_8));
			response.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");
			inboundChannel.write(response);
			closeOnFlush(inboundChannel);
			
			ChannelBufferUtils.logHttpRequest(request);
			logger.info("account disabled.");
			
			return;
		}

		/*
		 * 代表proxy向服务器发起请求
		 */
		worker.run(request, inboundChannel);

	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		closeOnFlush(e.getChannel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		logger.error(e);
		closeOnFlush(e.getChannel());
	}

	void closeOnFlush(Channel ch) {
		if (ch.isConnected()) {
			ch.write(ChannelBuffers.EMPTY_BUFFER).addListener(
					ChannelFutureListener.CLOSE);
		}
	}
}
