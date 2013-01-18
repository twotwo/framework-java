/**
 * Create at Jan 16, 2013
 */
package com.li3huo.netty.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;

import com.li3huo.netty.util.ServerInfo;

/**
 * @author liyan
 * 
 */
public class ConsoleHandler extends HttpRequestHandler {

	private final AtomicLong accessCount = new AtomicLong();

	private StringBuilder help = new StringBuilder();

	private Logger log;
	private ServiceContext context;

	public ConsoleHandler(int port, ServiceContext context) {
		super(port, context);
		this.log = Logger.getLogger("Server[" + port + "]");
		this.context = context;

		// create help hint
		help.append("Usage:\n");
		help.append("Actions\n");
		help.append("action=status\n");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.li3huo.netty.service.HttpRequestHandler#handleHttpRequest(org.jboss
	 * .netty.channel.MessageEvent,
	 * org.jboss.netty.handler.codec.http.HttpRequest)
	 */
	@Override
	public void handleHttpRequest(MessageEvent event, HttpRequest request) {

		accessCount.addAndGet(1);
		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(
				request.getUri());
		Map<String, List<String>> params = queryStringDecoder.getParameters();

		List<String> actions = params.get("action");
		if (null == actions) {
			this.writeResponse(event, help.toString());
			return;
		}

		for (String action : actions) {

			if ("status".equalsIgnoreCase(action)) {
				this.writeResponse(event, getStatusInfo());
				continue;
			} else if ("stop".equalsIgnoreCase(action)) {
				this.writeResponse(event, "Access Count: " + getAccessCount());
				log.info("stoped by console, from " + event.getRemoteAddress());
				System.exit(0);
			} else {
				this.writeResponse(event, help.toString());
				return;
			}
		}
	}

	private String getStatusInfo() {
		StringBuffer buffer = new StringBuffer();

		buffer.append("<pre>");

		buffer.append("\nServer Access Count: " + getAccessCount());
		buffer.append("\nConsole Access Count: " + accessCount);

		buffer.append("\n\n====Uptime");
		try {
			ServerInfo.loadUptime(buffer);
		} catch (IOException e) {
		}

		buffer.append("\n\n====System Info");
		try {
			ServerInfo.loadSystemInfo(buffer);
		} catch (IOException e) {
		}

		buffer.append("\n\n====Memory Info");
		try {
			ServerInfo.loadMemoryInfo(buffer);
		} catch (IOException e) {
		}

		buffer.append("\n\n====Thread Info");
		try {
			ServerInfo.loadThreadInfo(buffer);
		} catch (IOException e) {
		}

		buffer.append("</pre>");

		return buffer.toString();
	}

}
