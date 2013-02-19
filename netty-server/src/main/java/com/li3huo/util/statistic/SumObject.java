/**
 * Create at Feb 19, 2013
 */
package com.li3huo.util.statistic;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author liyan
 *
 */
public class SumObject {
	private String key;
	
	private AtomicLong accessCount;
	private AtomicLong costTime;

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 */
	public SumObject(String key) {
		this.key = key;
		
		this.accessCount = new AtomicLong();
		this.costTime = new AtomicLong();
	}
	
	/**
	 * 
	 * @param weight
	 * @param costNanoTime
	 */
	public void add(int weight, long costNanoTime) {
		// access time plus one
		this.accessCount.addAndGet(1);
		// add cost nano time
		this.costTime.addAndGet(costNanoTime);
	}
	
}
