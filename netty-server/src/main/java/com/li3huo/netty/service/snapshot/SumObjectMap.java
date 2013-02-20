/**
 * Create at Feb 19, 2013
 */
package com.li3huo.netty.service.snapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author liyan
 * 
 */
public class SumObjectMap {

	private Map<String, SumObject> map = new HashMap<String, SumObject>();

	private class SumObject {
		private String key;

		AtomicLong accessCount;
		AtomicLong costTime;

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
	}

	/**
	 * 
	 * @param weight
	 * @param costNanoTime
	 */
	public void add(String key, int weight, long costNanoTime) {

		if (null == map.get(key)) {
			map.put(key, new SumObject(key));
		}
		// access time plus one
		map.get(key).accessCount.addAndGet(weight);
		// add cost nano time
		map.get(key).costTime.addAndGet(costNanoTime);
	}

}
