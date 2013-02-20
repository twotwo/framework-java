package com.li3huo.netty.service;

import org.apache.log4j.Logger;

import com.li3huo.netty.service.snapshot.MessageWatch;
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
	 */
	public static byte[] process(MessageWatch watch, byte[] req) {
		watch.start(MessageWatch.State_Work);
		byte[] resp = null;
		try {

			/*
			 * get processor by request uri
			 */
			watch.getRequestUri();
			// new processor
			/*
			 * give Netty IO to processor
			 */
			// processor.process(watch, req)
			String content = "request size = ";
			if (null != req) {
				content += req.length;
			} else {
				content += "null";
			}
			resp = content.getBytes();

		} catch (Exception e) {
			watch.addException(e);
		}
		watch.stop(MessageWatch.State_Work);
		return resp;
	}

	/**
	 * call before close service
	 */
	public static void release() {
	}
}
