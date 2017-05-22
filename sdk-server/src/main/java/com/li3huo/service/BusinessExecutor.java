/**
 * BusinessExecutor.java create at Apr 14, 2017 3:39:22 PM
 */
package com.li3huo.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @ClassName: BusinessExecutor
 * @Description: TODO
 * @author liyan
 * @date Apr 14, 2017 3:39:22 PM
 *
 */
public class BusinessExecutor extends ThreadPoolExecutor {
	static final Logger logger = LogManager.getLogger(BusinessExecutor.class.getName());

	public BusinessExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		super.beforeExecute(t, r);
		logger.debug(" +++ Pre Execute Job " + " // Thread_Name " + t.getName());

	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		super.afterExecute(r, t);
		// System.out.println(" --- Post Execute Job " + ((Job) r).getJobName()
		// + " // Thread_Name " + t.getName());
		logger.debug(" --- Post Execute Job " + " // Thread_Name ");
	}

}