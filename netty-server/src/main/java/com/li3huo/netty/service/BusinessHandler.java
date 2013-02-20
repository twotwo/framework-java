/**
 * Create at Jan 22, 2013
 */
package com.li3huo.netty.service;

import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import com.li3huo.netty.service.snapshot.MessageWatch;

/**
 * @author liyan
 * 
 */
public class BusinessHandler extends HttpRequestHandler {
	private Logger log;

	// private ServiceContext context;

	public BusinessHandler(int port) {
		super(port);
		this.log = Logger.getLogger("Server[" + port + "]");
	}

	public void handleHttpRequest(MessageWatch watch) throws Exception {
		/**
		 * load request data in watch
		 */
		byte[] req = loadRequestData(watch.getRequest());

		/**
		 * ApplicationConfig hold all business logic
		 */
		byte[] resp = ApplicationConfig.process(watch, req);

		writeResponse(watch, resp);
	}

	private void writeResponse(MessageWatch watch, byte[] resp) {

		MessageEvent event = watch.getEvent();

		HttpResponse response = new DefaultHttpResponse(HTTP_1_1,
				HttpResponseStatus.valueOf(watch.getResponseStatus()));
		// response.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");
		response.setHeader(CONTENT_TYPE, "application/octet-stream"); // ?

		HttpRequest request = (HttpRequest) event.getMessage();

		// response.setHeader(header.getKey(), header.getValue());

		response.setHeader(CONTENT_LENGTH, resp.length);// 下发长度
		response.setContent(ChannelBuffers.copiedBuffer(resp));

		boolean keepAlive = isKeepAlive(request);

		if (keepAlive) {
			response.setHeader(CONTENT_LENGTH, response.getContent()
					.readableBytes());
			response.setHeader(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
		}

		log.debug("Channel Status [w=" + event.getChannel().isWritable()
				+ ", o=" + event.getChannel().isOpen() + "]");
		ChannelFuture future = event.getChannel().write(response);

		if (!keepAlive) {
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}
}
