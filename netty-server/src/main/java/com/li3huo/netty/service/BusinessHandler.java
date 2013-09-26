/**
 * Create at Jan 22, 2013
 */
package com.li3huo.netty.service;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.SERVER;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponse;

import com.li3huo.util.HttpsUtil;

/**
 * @author liyan
 * 
 */
public class BusinessHandler extends HttpRequestHandler {
	private Logger log;

	// private ServiceContext context;

	public BusinessHandler(int port) {
		super(port);
		this.log = Logger.getLogger("Server[" + port + "]");
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

		msgCtx.updateProcessStatus(HttpMessageContext.IN_BUSINESS);

		/**
		 * ApplicationConfig hold all business logic
		 */
		try {
			if (!msgCtx.requestUri.contains("/test")) {
				String proxyPass = "https://github.com";
				proxyPass = "https://172.27.236.207";

				HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);

				response.setHeader(SERVER, "Netty-HTTP/1.0");
				response.setHeader(CONTENT_TYPE, "text/html; charset=UTF-8");
				
				System.out.println("["+msgCtx.getRequest().getMethod()+"]"+proxyPass + msgCtx.requestUri);
				response.setContent(ChannelBuffers.copiedBuffer(HttpsUtil
						.doGet(proxyPass + msgCtx.requestUri)));
				return response;
			} else
				return ApplicationConfig.process(msgCtx);
		} catch (Exception ex) {
			log.fatal("ApplicationConfig process error.");
			throw ex;
		} finally {
			msgCtx.updateProcessStatus(HttpMessageContext.OUT_BUSINESS);
		}

	}

}
