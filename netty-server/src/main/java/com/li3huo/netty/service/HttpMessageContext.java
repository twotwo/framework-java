/**
 * Create at Feb 21, 2013
 */
package com.li3huo.netty.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.CompositeChannelBuffer;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import com.li3huo.business.BusinessException;
import com.li3huo.netty.service.snapshot.MessageWatch;

/**
 * @author liyan
 * 
 *         Each http process has a HttpMessageContext
 */
public class HttpMessageContext {

	private static Logger log = Logger.getLogger(HttpMessageContext.class
			.getName());

	MessageEvent event;
	private HttpRequest request;
	private MessageWatch watch;

	boolean keepAlive;
	String requestUri;

	/**
	 * @return the keepAlive
	 */
	public boolean isKeepAlive() {
		return keepAlive;
	}

	/**
	 * 
	 */
	public HttpMessageContext(MessageEvent event) {
		this.event = event;
		this.request = (HttpRequest) event.getMessage();
		this.watch = new MessageWatch(event);

		this.requestUri = request.getUri();

		keepAlive = HttpHeaders.isKeepAlive(request);
	}

	/*
	 * Release context and add watch to snapshot
	 */
	public void release() {
		// add messageWatch to Snapshot.
		ApplicationConfig.getSnapshotService().addMessageWatch(watch, exceptions);
		watch = null;
	}

	private List<Exception> exceptions = new ArrayList<Exception>();
	public void addException(Exception ex) {

		exceptions.add(ex);
		int responseCode;
		if (ex instanceof BusinessException) {
			responseCode = HttpResponseStatus.INTERNAL_SERVER_ERROR.getCode();
			log.fatal("code[" + responseCode + ",error[" + ex.getMessage()
					+ "]");
		} else if (ex instanceof HttpException) {
			responseCode = HttpResponseStatus.BAD_REQUEST.getCode();
		} else {
			responseCode = HttpResponseStatus.NOT_IMPLEMENTED.getCode();
			log.warn("code[" + responseCode + ",error[" + ex.getMessage() + "]");
		}

		watch.setResponseCode(responseCode);
		log.fatal("from: " + watch.getRemoteIP() + ", access: "
				+ watch.getRequestUri() + " at " + ex.getMessage());
	}

	public int getResponseCode() {
		return watch.getResponseCode();
	}

	public byte[] loadRequestData() {
		byte[] bytes = null;
		if (request.getContent() instanceof CompositeChannelBuffer) {
			int i = ((CompositeChannelBuffer) request.getContent()).capacity();
			bytes = new byte[i];
			request.getContent().getBytes(0, bytes, 0, i);
		} else {
			bytes = request.getContent().array();
		}
		return bytes;
	}

	/**
	 * @return the request
	 */
	public HttpRequest getRequest() {
		return request;
	}

	public static final int IN_BUSINESS = 1;
	public static final int OUT_BUSINESS = 2;

	public void updateProcessStatus(int state) {
		if (state == IN_BUSINESS) {
			watch.setBusiness();
		} else if (state == OUT_BUSINESS) {
			watch.stop(MessageWatch.STATE_BUSINESS);
		}
	}

}
