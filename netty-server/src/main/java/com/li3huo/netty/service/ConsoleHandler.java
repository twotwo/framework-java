/**
 * Create at Jan 16, 2013
 */
package com.li3huo.netty.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;

import com.li3huo.netty.service.snapshot.MessageWatch;
import com.li3huo.netty.service.snapshot.SnapshotService;
import com.li3huo.util.ServerInfo;

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
	// private ServiceContext context;
	private SnapshotService snapshot = ApplicationConfig.getSnapshotService();

	public ConsoleHandler(int port) {
		super(port);
		this.log = Logger.getLogger("Server[" + port + "]");

		// create help hint
		consoleHelpInfo.append("<html>");
		consoleHelpInfo.append("\n<body bgcolor=\"white\">");
		consoleHelpInfo.append("<h1> Netty Server Console </h1>");
		consoleHelpInfo.append("<br>Usage:\n");
		consoleHelpInfo.append("<br>Actions\n");
		consoleHelpInfo
				.append("<br><a href=\"console?action=status\">status</a>\n");
		consoleHelpInfo
				.append("<br><a href=\"console?action=snapshot\">snapshot</a>\n");
		consoleHelpInfo.append("<br>\n<h4>\n</font>\n</body>\n</html>");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.li3huo.netty.service.HttpRequestHandler#handleHttpRequest(com.li3huo
	 * .netty.service.snapshot.MessageWatch)
	 */
	@Override
	public void handleHttpRequest(MessageWatch watch) throws Exception {

		MessageEvent event = watch.getEvent();
		HttpRequest request = watch.getRequest();

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
			} else if ("snapshot".equalsIgnoreCase(action)) {
				this.writeResponse(event, getSnapshotInfo());
				continue;
			} else if ("stop".equalsIgnoreCase(action)) {
				this.writeResponse(event, snapshot.getAccessLog());
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
		buffer.append("<h1>Netty Server Console</h1>");

		buffer.append("<h2>Access Log</h2>").append(snapshot.getAccessLog())
				.append("<br>");

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

	/**
	 * 
	 * @return
	 */
	private String getSnapshotInfo() {
		StringBuffer buffer = new StringBuffer();

		buffer.append("<html>");
		buffer.append("\n<body bgcolor=\"white\">");
		buffer.append("<h1>Netty Server Console</h1>\n");
		buffer.append("<h2>Snapshot at " + new Date() + "</h2>\n");
		buffer.append("<h3>Http Access Summary</h3>\n");
		buffer.append(snapshot.getAccessLog());
		buffer.append("<h3>Bisiness Process Summary</h3>\n");
		buffer.append(snapshot.getAccessLog());
		buffer.append("\n</body>\n</html>");

		return buffer.toString();
	}
}
