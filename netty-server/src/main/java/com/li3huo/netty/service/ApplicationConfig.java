package com.li3huo.netty.service;

import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.HttpResponse;

import com.li3huo.business.RandomErrorProcessor;
import com.li3huo.netty.service.snapshot.SnapshotService;

/**
 * Application Container
 * 
 * Initialise Application by Configuration, and provide overall business
 * logic(Facade)
 * 
 * @author liyan
 * 
 */
public class ApplicationConfig {

	private static Logger log = Logger.getLogger(ApplicationConfig.class
			.getName());
	private static SnapshotService snapshot = null;

	/**
	 * @return the snapshot
	 */
	public static SnapshotService getSnapshotService() {
		return snapshot;
	}

	/**
	 * Initialise Application by Configuration
	 */
	public static void init() {

		snapshot = new SnapshotService();
		log.info("App init...");
	}

	/**
	 * the overall business process logic
	 * 
	 * @param watch
	 * @throws Exception 
	 */
	public static HttpResponse process(HttpMessageContext msgCtx) throws Exception {
		/*
		 * get processor by request uri
		 */
		RandomErrorProcessor processor = new RandomErrorProcessor(msgCtx.requestUri);

		return processor.process(msgCtx);
	}

	/**
	 * call before close service
	 */
	public static void release() {
	}
}
