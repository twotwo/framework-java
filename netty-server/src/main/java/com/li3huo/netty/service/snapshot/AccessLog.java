/**
 * Create at Feb 19, 2013
 */
package com.li3huo.netty.service.snapshot;

import java.net.InetSocketAddress;

import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;

/**
 * @author liyan
 * 
 * Log for Every Http Request
 * @deprecated use MessageWatch instead
 */
public class AccessLog {
	private long createTime;
	private HttpRequest request;
	private String remoteIP;

	/**
	 * 
	 */
	public AccessLog(MessageEvent event) {
		this.createTime = System.nanoTime();
		this.request = (HttpRequest) event.getMessage();
		this.remoteIP = ((InetSocketAddress)event.getRemoteAddress()).getAddress().toString();
	}
	
	
	public String getAccessKey() {
		return HttpHeaders.getHost(request, "") + request.getUri();
	}
	
	/**
	 * @return the remoteIP
	 */
	public String getRemoteIP() {
		return remoteIP;
	}


	public long getAliveTime() {
		return System.nanoTime() - createTime;
	}
}
