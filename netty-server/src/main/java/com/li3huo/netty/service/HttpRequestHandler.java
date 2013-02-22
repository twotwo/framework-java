/**
 * Create at Jan 16, 2013
 */
package com.li3huo.netty.service;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.util.CharsetUtil;

import com.li3huo.business.BaseProcessor;

/**
 * @author liyan
 * 
 */
public class HttpRequestHandler extends SimpleChannelUpstreamHandler {

	private Logger log;

	/**
	 * 
	 * @param port
	 *            - listening port
	 */
	public HttpRequestHandler(int port) {
		this.log = Logger.getLogger("Server[" + port + "]");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jboss.netty.channel.SimpleChannelUpstreamHandler#messageReceived(
	 * org.jboss.netty.channel.ChannelHandlerContext,
	 * org.jboss.netty.channel.MessageEvent)
	 * 
	 * Create HttpMessageContext & call getHttpResponse(HttpMessageContext)
	 * 
	 * If get HttpResponse, call writeResponse
	 * 
	 * else, call writeErrorResponse
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent event)
			throws Exception {

		HttpMessageContext msgCtx = new HttpMessageContext(event);
		try {
			HttpResponse content = getHttpResponse(msgCtx);
			writeResponse(msgCtx, content);
		} catch (Exception ex) {
			msgCtx.addException(ex);
			writeErrorResponse(msgCtx);
		} finally {
			msgCtx.release();
			msgCtx = null;
		}
	}

	/**
	 * Base method for Business/Console Handler
	 * 
	 * @param msgCtx
	 * @return
	 * @throws Exception
	 */
	public HttpResponse getHttpResponse(HttpMessageContext msgCtx)
			throws Exception {
		BaseProcessor processor = new BaseProcessor();

		return processor.process(msgCtx);
	}

	/**
	 * Write Normal Content to Client
	 * 
	 * @param msgCtx
	 * @param response
	 * @throws Exception
	 */
	public void writeResponse(HttpMessageContext msgCtx, HttpResponse response)
			throws Exception {

		// Write the response.
		log.debug("Channel Status [w=" + msgCtx.event.getChannel().isWritable()
				+ ", o=" + msgCtx.event.getChannel().isOpen() + "]");
		ChannelFuture future = null;
		try {
			future = msgCtx.event.getChannel().write(response);
		} catch (Exception ex) {
			ex.initCause(ex);
			throw ex;
		} finally {

			if (!msgCtx.isKeepAlive() && null != future) {
				future.addListener(ChannelFutureListener.CLOSE);
			}
		}
	}

	/**
	 * Write Error Info to Client
	 * 
	 * @param msgCtx
	 */
	public void writeErrorResponse(HttpMessageContext msgCtx) {
		HttpResponse response = new DefaultHttpResponse(HTTP_1_1,
				HttpResponseStatus.valueOf(msgCtx.getResponseCode()));
		response.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");
		response.setContent(ChannelBuffers.copiedBuffer(
				"Failure: " + msgCtx.toString() + "\r\n", CharsetUtil.UTF_8));
		// Close the connection as soon as the error message is sent.
		msgCtx.event.getChannel().write(response)
				.addListener(ChannelFutureListener.CLOSE);
	}
}