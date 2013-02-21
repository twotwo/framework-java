/**
 * Create at Jan 22, 2013
 */
package com.li3huo.netty.service;

import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.HttpResponse;

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
			return ApplicationConfig.process(msgCtx);
		} catch (Exception ex) {
			log.fatal("ApplicationConfig process error.");
			throw ex;
		} finally {
			msgCtx.updateProcessStatus(HttpMessageContext.OUT_BUSINESS);
		}

	}

}
