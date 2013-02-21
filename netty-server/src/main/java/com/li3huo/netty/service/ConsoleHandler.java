/**
 * Create at Jan 16, 2013
 */
package com.li3huo.netty.service;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.SERVER;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.util.CharsetUtil;

import com.li3huo.netty.service.snapshot.SnapshotService;
import com.li3huo.util.ServerInfo;

/**
 * @author liyan
 * 
 */
public class ConsoleHandler extends HttpRequestHandler {

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
	 * .netty.service.HttpMessageContext)
	 */
	@Override
	public HttpResponse getHttpResponse(HttpMessageContext msgCtx)
			throws Exception {

		HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);

		response.setHeader(SERVER, "Netty-HTTP/1.0");
		response.setHeader(CONTENT_TYPE, "text/html; charset=UTF-8");

		if (!msgCtx.requestUri.contains("/console")) {
			response.setContent(ChannelBuffers.copiedBuffer(
					consoleHelpInfo.toString(), CharsetUtil.UTF_8));
		} else {
			response.setContent(ChannelBuffers.copiedBuffer(
					makeConsoleResponse(msgCtx.getRequest()), CharsetUtil.UTF_8));
		}

		if (msgCtx.isKeepAlive()) {
			// Add 'Content-Length' header only for a keep-alive connection.
			response.setHeader(CONTENT_LENGTH, response.getContent()
					.readableBytes());
			// Add keep alive header as per:
			// -
			// http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
			response.setHeader(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
		}

		return response;

	}

	private String makeConsoleResponse(HttpRequest request) {

		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(
				request.getUri());
		Map<String, List<String>> params = queryStringDecoder.getParameters();

		List<String> actions = params.get("action");
		if (null == actions) {
			return consoleHelpInfo.toString();
		}
		
		log.info("actions="+actions);

		for (String action : actions) {
			
			if ("status".equalsIgnoreCase(action)) {
				return getStatusInfo();
			} else if ("snapshot".equalsIgnoreCase(action)) {
				return getSnapshotInfo();
			} else if ("stop".equalsIgnoreCase(action)) {
				String actionInfo = "stoped by console.";
				log.info(actionInfo);
				// System.exit(0);
			}

		}
		return consoleHelpInfo.toString();
	}

	private String getStatusInfo() {
		StringBuffer buffer = new StringBuffer();

		buffer.append("<html>");
		buffer.append("\n<body bgcolor=\"white\">");
		buffer.append("<h1>Netty Server Console</h1>");

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
		buffer.append("<h2>Snapshot query at " + new Date() + "</h2>\n");
		// buffer.append("<h3>Http Access Summary</h3>\n");
		// buffer.append(snapshot.getAccessLog());
		// buffer.append("<h3>Bisiness Process Summary</h3>\n");
		buffer.append(snapshot.getWatchInfo(0, 1));
		buffer.append("\n</body>\n</html>");

		return buffer.toString();
	}
}
