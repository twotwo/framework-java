/**
 * 
 */
package com.li3huo.netty;

import static org.junit.Assert.assertNotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.*;

/**
 * @author liyan
 * 
 */
public class ExecutorServiceTest {
	static ExecutorService executor;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		executor = Executors.newCachedThreadPool();
	}

	@AfterClass
	public static void releaseResource() throws Exception {
		executor.shutdown();
	}

	class SampleThread implements Runnable{
		private int count = 1, number;

		public SampleThread(int num) {
			number = num;
			System.out.println("Create Thread-" + number);
		}

		public void run() {
			while (true) {
				System.out.println("Thread-" + number + " run " + count
						+ " time(s)");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (++count == 10)
					return;
			}
		}
	}

	@Test
	public void runThread() throws Exception {
		assertNotNull(executor);
		for (int i = 0; i < 10; i++) {
			executor.execute(new SampleThread(i));
		}
	}
}
