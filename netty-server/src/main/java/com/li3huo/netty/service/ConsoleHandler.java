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

	/**
	 * record for console access count
	 */
	private AtomicLong consoleAccessCount = new AtomicLong();

	private StringBuilder consoleHelpInfo = new StringBuilder();

	private Logger log;
	private ServiceContext context;

	public ConsoleHandler(int port, ServiceContext context) {
		super(port, context);
		this.log = Logger.getLogger("Server[" + port + "]");
		this.context = context;

		// create help hint
		consoleHelpInfo.append("<html>");
		consoleHelpInfo.append("\n<body bgcolor=\"white\">");
		consoleHelpInfo.append("<h1> Netty Server Console </h1>");
		consoleHelpInfo.append("<br>Usage:\n");
		consoleHelpInfo.append("<br>Actions\n");
		consoleHelpInfo
				.append("<br><a href=\"console?action=status\">status</a>\n");
		consoleHelpInfo.append("<br>\n<h4>\n</font>\n</body>\n</html>");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see HttpRequestHandler#handleHttpRequest(org.jboss
	 * .netty.channel.MessageEvent,
	 * org.jboss.netty.handler.codec.http.HttpRequest)
	 */
	@Override
	public void handleHttpRequest(MessageEvent event, HttpRequest request)
			throws Exception {

		if (!request.getUri().startsWith("/console")) {
			this.writeResponse(event, consoleHelpInfo.toString());
			return;
		}
		consoleAccessCount.addAndGet(1);
		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(
				request.getUri());
		Map<String, List<String>> params = queryStringDecoder.getParameters();

		List<String> actions = params.get("action");
		if (null == actions) {
			this.writeResponse(event, consoleHelpInfo.toString());
			return;
		}

		for (String action : actions) {

			if ("status".equalsIgnoreCase(action)) {
				this.writeResponse(event, getStatusInfo());
				continue;
			} else if ("stop".equalsIgnoreCase(action)) {
				this.writeResponse(event, context.getSnapshotService()
						.getAccessLog());
				log.info("stoped by console, from " + event.getRemoteAddress());
				// System.exit(0);
			} else {
				this.writeResponse(event, consoleHelpInfo.toString());
				return;
			}
		}
	}

	private String getStatusInfo() {
		StringBuffer buffer = new StringBuffer();

		buffer.append("<html>");
		buffer.append("\n<body bgcolor=\"white\">");
		buffer.append("<h1> Netty Server Console </h1>");

		buffer.append("<h2>Access Log</h2>")
				.append(context.getSnapshotService().getAccessLog()).append("<br>");

		buffer.append("\n<br>Valid Console Access Count: " + consoleAccessCount);

		buffer.append("<pre>");
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
		buffer.append("<br>\n<h4>\n</font>\n</body>\n</html>");

		return buffer.toString();
	}

}
