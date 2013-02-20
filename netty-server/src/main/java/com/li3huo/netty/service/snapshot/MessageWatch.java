/**
 * Create at Feb 20, 2013
 */
package com.li3huo.netty.service.snapshot;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import com.li3huo.netty.service.BusinessException;
import com.li3huo.netty.service.HttpException;

/**
 * @author liyan
 * 
 *         Netty message context: each request has own instance, every process
 *         add state to it.
 * 
 *         pay my respects to org.apache.commons.lang.time.StopWatch
 */
public class MessageWatch {
	
	private Logger log = Logger.getLogger(MessageWatch.class.getName());
	
	public static final int State_All = 0;
	public static final int State_Work = 1;

	/**
	 * 5 stopwatch, each has starttime&endtime
	 */
	private long[][] stopwatch = new long[5][2];

	private MessageEvent event;
	private HttpRequest request;
	private String remoteIP;

	/**
	 * 
	 */
	public MessageWatch(MessageEvent event) {
		this.stopwatch[State_All][0] = System.nanoTime();
		this.event = event;
		this.request = (HttpRequest) event.getMessage();
		this.remoteIP = ((InetSocketAddress) event.getRemoteAddress())
				.getAddress().toString();
	}

	public void start(int state) {
		this.stopwatch[state][0] = System.nanoTime();
	}

	public void stop(int state) {
		this.stopwatch[state][1] = System.nanoTime();
	}

	public String getRequestUri() {
		return HttpHeaders.getHost(request, "") + request.getUri();
	}

	/**
	 * @return the remoteIP
	 */
	public String getRemoteIP() {
		return remoteIP;
	}

	public long getAliveTime() {
		if (stopwatch[State_All][1] == 0) {
			stopwatch[State_All][1] = System.nanoTime();
		}
		return stopwatch[State_All][1] - stopwatch[State_All][0];
	}

	public long getAliveTime(int state) {
		if (state >= stopwatch.length) {
			return -1;
		}
		if (stopwatch[State_All][1] == 0) {
			stopwatch[state][1] = System.nanoTime();
		}
		return stopwatch[state][1] - stopwatch[state][0];
	}

	/**
	 * Exception in business process
	 * 
	 * @param ex
	 */
	public void addException(Exception ex) {
		if (ex instanceof BusinessException) {
			responseCode = HttpResponseStatus.INTERNAL_SERVER_ERROR.getCode();
			log.fatal("code["+responseCode+",error["+ex.getMessage()+"]");
		} else if (ex instanceof HttpException) {
			responseCode = HttpResponseStatus.BAD_REQUEST.getCode();
		} else {
			responseCode = HttpResponseStatus.NOT_IMPLEMENTED.getCode();
			log.warn("code["+responseCode+",error["+ex.getMessage()+"]");
		}
	}

	private int responseCode = 200;

	public int getResponseStatus() {
		return responseCode;
	}

	/**
	 * @return the event
	 */
	public MessageEvent getEvent() {
		return event;
	}

	/**
	 * @return the request
	 */
	public HttpRequest getRequest() {
		return request;
	}

}
