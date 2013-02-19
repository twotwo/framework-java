package com.li3huo.netty.service.snapshot;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Access Statics
 * @author liyan
 *
 */
class AccessStatics {

	private String key;
	private AtomicLong accessCount;
	/**
	 * @return the accessCount
	 */
	public AtomicLong getAccessCount() {
		return accessCount;
	}

	/**
	 * @return the costTime
	 */
	public AtomicLong getCostTime() {
		return costTime;
	}


	private AtomicLong costTime;

	public AccessStatics(String key) {

		this.key = key;

		this.accessCount = new AtomicLong();
		this.costTime = new AtomicLong();
	}

	public String toString() {
		return this.key;
	}

	/**
	 * @deprecated use addLog(AccessLog)
	 * @param costNanoTime
	 */
	public void logAccess(long costNanoTime) {
		// access time plus one
		this.accessCount.addAndGet(1);
		// add cost nano time
		this.costTime.addAndGet(costNanoTime);
	}
	
	public void addLog(AccessLog httpLog) {
		// access time plus one
		this.accessCount.addAndGet(1);
		// add cost nano time
		this.costTime.addAndGet(httpLog.getAliveTime());
	}

	public long getAverageCost() {
		return costTime.get() / accessCount.get() / 1000000;
	}

}