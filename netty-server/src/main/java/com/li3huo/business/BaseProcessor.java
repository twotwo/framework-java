/**
 * Create at Feb 21, 2013
 */
package com.li3huo.business;

import static org.jboss.netty.handler.codec.http.HttpHeaders.getHost;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.SERVER;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.util.CharsetUtil;

import com.li3huo.netty.service.HttpMessageContext;

/**
 * @author liyan
 *
 */
public class BaseProcessor {
	
	public HttpResponse process(HttpMessageContext msgCtx) throws Exception{
		
		HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);

		response.setHeader(SERVER, "Netty-HTTP/1.0");
		response.setHeader(CONTENT_TYPE, "text/html; charset=UTF-8");
		
		response.setContent(ChannelBuffers.copiedBuffer(
				makeTestContent(msgCtx.getRequest()),
				CharsetUtil.UTF_8));
		
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

	public StringBuilder makeTestContent(HttpRequest request)
			throws UnsupportedEncodingException {
		StringBuilder buf = new StringBuilder();
		buf.append("<html>");
		buf.append("\n<body bgcolor=\"white\">");
		buf.append("<h1> Request Information </h1>");
		buf.append("<font size=\"4\">");
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

		buf.append("<!--          30           -->");
		buf.append("<!--          30           -->");
		buf.append("<!--          30           -->");
		buf.append("<!--          30           -->");
		buf.append("<!--          30           -->");
		buf.append("<!--          30           -->");
		buf.append("<!--          30           -->");
		buf.append("<!--          30           -->");
		buf.append("<!--          30           -->");
		buf.append("<!--          30           -->");

		buf.append("<br>\n<h4>\n</font>\n</body>\n</html>");

		return buf;
	}
}
