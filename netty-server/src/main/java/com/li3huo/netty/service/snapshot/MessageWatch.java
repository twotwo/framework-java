/**
 * Create at Feb 20, 2013
 */
package com.li3huo.netty.service.snapshot;

import java.net.InetSocketAddress;

import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;

/**
 * @author liyan
 * 
 *         Netty message context: each request has own instance, every process
 *         add state to it.
 * 
 *         pay my respects to org.apache.commons.lang.time.StopWatch
 */
public class MessageWatch {
	
//	private Logger log = Logger.getLogger(MessageWatch.class.getName());
	
	public static final int STATE_ALL = 0;
	public static final int STATE_BUSINESS = 1;

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
		this.stopwatch[STATE_ALL][0] = System.nanoTime();
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
		return request.getUri();
	}
	
	public String getHost() {
		return HttpHeaders.getHost(request, "");
	}

	/**
	 * @return the remoteIP
	 */
	public String getRemoteIP() {
		return remoteIP;
	}

	public long getAliveTime() {
		if (stopwatch[STATE_ALL][1] == 0) {
			stopwatch[STATE_ALL][1] = System.nanoTime();
		}
		return stopwatch[STATE_ALL][1] - stopwatch[STATE_ALL][0];
	}

	public long getAliveTime(int state) {
		if (state >= stopwatch.length) {
			return -1;
		}
		if (stopwatch[STATE_ALL][1] == 0) {
			stopwatch[state][1] = System.nanoTime();
		}
		return stopwatch[state][1] - stopwatch[state][0];
	}

	private int responseCode = 200;

	public int getResponseCode() {
		return responseCode;
	}

	/**
	 * @param responseCode the responseCode to set
	 */
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
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
	
	boolean bool_busi = false;
	public boolean isBusiness() {
		return bool_busi;
	}
	public void setBusiness() {
		bool_busi = true;
		this.start(MessageWatch.STATE_BUSINESS);
	}
}
