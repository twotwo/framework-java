/**
 * Create at Jan 17, 2013
 */
package com.li3huo.netty.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpRequest;

/**
 * @author liyan
 * 
 */
public class ServiceContext {

	private Logger logger = Logger.getLogger(ServiceContext.class.getName());
	private static Map<String, AtomicLong> accessLog = new HashMap<String, AtomicLong>();
	private static AtomicLong costTime = new AtomicLong();

	int businessPort, consolePort;
	NioServerSocketChannelFactory businessServer, consoleServer;

	public void setBusinessServer(int businessPort,
			NioServerSocketChannelFactory businessServer) {
		this.businessPort = businessPort;
		this.businessServer = businessServer;
	}

	public void setConsoleServer(int consolePort,
			NioServerSocketChannelFactory consoleServer) {
		this.consolePort = consolePort;
		this.consoleServer = consoleServer;
	}

	public synchronized void logAccess(HttpRequest request) {
		if (null == accessLog.get(request.getUri())) {
			accessLog.put(request.getUri(), new AtomicLong());
		}
		accessLog.get(request.getUri()).addAndGet(1);
	}

	public long getAccessCount() {
		long count = 0;
		for (AtomicLong l : accessLog.values()) {
			count += l.get();
		}
		return count;
	}

	public String getAccessLog() {
		StringBuffer buf = new StringBuffer();
		buf.append("<table>").append(
				"<tr><td><b>URI</b>\t</td><td><b>Count</b></td></tr>\n");
		for (Map.Entry<String, AtomicLong> log : accessLog.entrySet()) {
			buf.append("<tr><td>").append(log.getKey()).append("</td><td>")
					.append(log.getValue().get()).append("</td></tr>\n");
		}
		buf.append("</table>");
		return buf.toString();
	}

	public AtomicLong getCostTime() {
		return costTime;
	}

	public Logger getLogger() {
		return logger;
	}
}
