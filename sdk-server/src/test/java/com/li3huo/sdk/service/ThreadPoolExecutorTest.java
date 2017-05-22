/**
 * ThreadPoolExecutorTest.java create at Apr 14, 2017 11:34:23 AM
 */
package com.li3huo.sdk.service;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @ClassName: ThreadPoolExecutorTest
 * @Description: TODO
 * @author liyan
 * @date Apr 14, 2017 11:34:23 AM
 *
 */
public class ThreadPoolExecutorTest {
	static final Logger logger = LogManager.getLogger(ThreadPoolExecutorTest.class.getName());

	/** max working numbers */
	static int maximumPoolSize = 5;
	static BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(maximumPoolSize);
	/** the executor */
	static ThreadPoolExecutor executor;

	/**
	 * @throws java.lang.Exception
	 * 
	 *             init ThreadPoolExecutor
	 */
	@BeforeClass
	public static void setUpBeforeClass() {

		int corePoolSize = 2;
		int keepAliveTime = 10;
		TimeUnit unit = TimeUnit.SECONDS;

		executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		
		executor.setRejectedExecutionHandler( new RejectedExecutionHandler() {

	          @Override
	          public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
	           
	              logger.debug("Reject job : ");
	              logger.debug("Wait a second");
	              try {
	                   Thread.sleep(1000);
	              } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	              logger.debug("Reenter job : ");
	              executor.execute(r);
	          }
	});

		executor.prestartAllCoreThreads();
	}

	@Test
	public void test() throws InterruptedException {
		Integer jobCounter = 1;
		while (true) {

			logger.debug("Add job : " + jobCounter);
			Thread.sleep(100);
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});

			if (jobCounter % 5 == 0)
				logger.debug(" ------------------------" + " working numbers = " + workQueue.size());
			Assert.assertFalse(workQueue.size() > maximumPoolSize);

			if (jobCounter++ > 10) {
				executor.shutdown();
				break;
			}
		}
	}
}
