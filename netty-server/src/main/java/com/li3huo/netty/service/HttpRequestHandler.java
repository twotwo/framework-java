/**
 * Create at Jan 16, 2013
 */
package com.li3huo.netty.service;

import static org.jboss.netty.handler.codec.http.HttpHeaders.getHost;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.SERVER;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.UnsupportedEncodingException;
import java.nio.channels.ClosedChannelException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.util.CharsetUtil;

/**
 * @author liyan
 * 
 */
public class HttpRequestHandler extends SimpleChannelUpstreamHandler {
	private Logger log;
	private ServiceContext context;

	public HttpRequestHandler(int port, ServiceContext context) {
		this.log = Logger.getLogger("Server[" + port + "]");
		this.context = context;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) {
		HttpRequest request = (HttpRequest) event.getMessage();

		try {
			handleHttpRequest(event, request);
		} catch (Exception e) {
			log.log(Level.SEVERE,
					"Unexpected exception from handleHttpRequest."
							+ e.getMessage());
		} finally {
			// log access info
			context.logAccess(request);
		}
	}

	public void handleHttpRequest(MessageEvent event, HttpRequest request)
			throws Exception {
		log.info("handleHttpRequest");
		try {
			writeResponse(event, makeTestContent(request).toString());
		} catch (UnsupportedEncodingException e) {
			log.log(Level.WARNING, "Unexpected exception from downstream.",
					e.getCause());
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {

		Throwable cause = e.getCause();
		if (cause instanceof TooLongFrameException) {
			sendError(ctx, BAD_REQUEST);
			return;
		}

		cause.printStackTrace();
		if (e.getChannel().isConnected()) {
			sendError(ctx, INTERNAL_SERVER_ERROR);
		}
	}

	private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
		HttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);
		response.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");
		response.setContent(ChannelBuffers.copiedBuffer(
				"Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8));
		log.log(Level.WARNING, "Error code: " + status.toString());
		// Close the connection as soon as the error message is sent.
		ctx.getChannel().write(response)
				.addListener(ChannelFutureListener.CLOSE);
	}

	/**
	 * Common Method for this handler
	 * 
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public StringBuilder parseRequestParameter(HttpRequest request)
			throws UnsupportedEncodingException {
		StringBuilder buf = new StringBuilder();
		buf.setLength(0);
		buf.append("WELCOME TO THE WILD WILD WEB SERVER\r\n");
		buf.append("===================================\r\n");

		buf.append("VERSION: " + request.getProtocolVersion() + "\r\n");
		buf.append("HOSTNAME: " + getHost(request, "unknown") + "\r\n");
		buf.append("REQUEST_URI: " + request.getUri() + "\r\n\r\n");

		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(
				request.getUri());
		Map<String, List<String>> params = queryStringDecoder.getParameters();
		if (!params.isEmpty()) {
			for (Entry<String, List<String>> p : params.entrySet()) {
				String key = p.getKey();
				List<String> vals = p.getValue();
				for (String val : vals) {
					buf.append("PARAM: " + key + " = " + val + "\r\n");
				}
			}
			buf.append("\r\n");
		}
		return buf;
	}

	/**
	 * Common Method for this handler
	 * 
	 * @param e
	 * @param content
	 */
	public void writeResponse(MessageEvent event, String content)
			throws Exception {
		// Build the response object.
		HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);

		response.setContent(ChannelBuffers.copiedBuffer(content,
				CharsetUtil.UTF_8));
		response.setHeader(SERVER, "Netty-HTTP/1.0");
		response.setHeader(CONTENT_TYPE, "text/html; charset=UTF-8");

		HttpRequest request = (HttpRequest) event.getMessage();
		boolean keepAlive = isKeepAlive(request);

		if (keepAlive) {
			// Add 'Content-Length' header only for a keep-alive connection.
			response.setHeader(CONTENT_LENGTH, response.getContent()
					.readableBytes());
			// Add keep alive header as per:
			// -
			// http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
			response.setHeader(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
		}

		if (!event.getChannel().isWritable()) {
			throw new ClosedChannelException();
		}

		// Write the response.
		ChannelFuture future = event.getChannel().write(response);

		if (!keepAlive) {
			future.addListener(ChannelFutureListener.CLOSE);
		}

	}

	public StringBuilder makeTestContent(HttpRequest request)
			throws UnsupportedEncodingException {
		StringBuilder buf = new StringBuilder();
		buf.append("<html>");
		buf.append("\n<body bgcolor=\"white\">");
		buf.append("<h1> Request Information </h1>");
		buf.append("<font size=\"4\">");
		buf.append("\nAccess Count: ").append(context.getAccessCount());
		buf.append("<br>\nJSP Request Method: ").append(request.getMethod());
		buf.append("<br>\nRequest URI: ").append(request.getUri());
		buf.append("<br>\nRequest Protocol: ").append(
				request.getProtocolVersion());
		buf.append("<br>\nContent length: ").append(
				request.getHeader(CONTENT_LENGTH));
		buf.append("<br>\nContent type: ").append(
				request.getHeader(CONTENT_TYPE));
		buf.append("<br>\nServer name: ").append(request.getHeader(SERVER));

		buf.append("<br>\nRemote host: ").append(getHost(request, "unknown"));

		buf.append("<br>\nAverage Execute Cost Time: ")
				.append(context.getCostTime().get() / context.getAccessCount()
						/ 1000000).append(" ms.");
		buf.append("<br>\nTotal Cost Time: ")
				.append(context.getCostTime().get() / 1000000).append(" ms.");
		buf.append("<!--                                                                                                                                    -->");
		buf.append("<br>\n<h4>\n</font>\n</body>\n</html>");

		return buf;
	}
}