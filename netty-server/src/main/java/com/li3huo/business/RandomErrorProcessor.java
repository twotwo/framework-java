/**
 * Create at Feb 21, 2013
 */
package com.li3huo.business;

import java.util.Random;

import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.HttpResponse;

import com.li3huo.netty.service.HttpException;
import com.li3huo.netty.service.HttpMessageContext;

/**
 * @author liyan a demo processor
 */
public class RandomErrorProcessor extends BaseProcessor {
	private static Logger log = Logger.getLogger(RandomErrorProcessor.class
			.getName());

	public RandomErrorProcessor(String requestUri) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.li3huo.business.BaseProcessor#process(com.li3huo.netty.service.
	 * HttpMessageContext)
	 */
	@Override
	public HttpResponse process(HttpMessageContext msgCtx) throws Exception {

		Random errorRandom = new Random();
		int code = (int) (errorRandom.nextFloat() * 10);
		log.debug("Random code=" + code);
		if (code < 6) {
			return super.process(msgCtx);
		} else if (code < 9) {
			throw new HttpException("fake http exception");
		} else {
			errorRandom = new Random();
			code = (int) (errorRandom.nextFloat() * 10);
			if (code < 4) {
				throw new ParseRequestException("fake parse exception");
			} else if (code < 6) {
				throw new AuthenticationException("fake auth exception");
			} else if (code < 8) {
				throw new HandleRequestException("fake handle exception");
			}
			throw new BusinessException("fake business exception");
		}
	}

}
