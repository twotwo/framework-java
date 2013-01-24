/**
 * Create at Jan 24, 2013
 */
package com.li3huo.netty.service;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;

/**
 * @author liyan
 * 
 */
public class SnapshotService {
	private static Map<String, AccessLog> accessLog = new HashMap<String, AccessLog>();
	DecimalFormat format = new DecimalFormat("#,##0.### ms");

	private class AccessLog {
		private String key;
		private AtomicLong accessCount, costTime;

		public AccessLog(HttpRequest request) {

			this.key = getAccessKey(request);

			this.accessCount = new AtomicLong();
			this.costTime = new AtomicLong();
		}

		public String toString() {
			return this.key;
		}

		public void logAccess(long costNanoTime) {
			// access time plus one
			this.accessCount.addAndGet(1);
			// add cost nano time
			this.costTime.addAndGet(costNanoTime);
		}

		public String getAverageCost() {
			return format.format(costTime.get() / accessCount.get() / 1000000);
		}

	}

	public String getAccessKey(HttpRequest request) {
		return HttpHeaders.getHost(request, "") + request.getUri();
	}

	/**
	 * log for access uri and cost time
	 * 
	 * @param request
	 * @param costNanoTime
	 */
	public synchronized void logAccess(HttpRequest request, long costNanoTime) {
		String key = getAccessKey(request);
		if (null == accessLog.get(key)) {
			accessLog.put(key, new AccessLog(request));
		}
		accessLog.get(key).logAccess(costNanoTime);
	}

	public String getAccessLog() {
		StringBuffer buf = new StringBuffer();

		long totalCount = 0, totolCost = 0;

		buf.append("<h3>Access Details</h3>")
				.append("<table border=\"1\">")
				.append("<tr><td><b>URI</b>\t</td><td><b>Count</b></td><td><b>Average Cost(ms)</b></td></tr>\n");
		for (Map.Entry<String, AccessLog> log : accessLog.entrySet()) {
			buf.append("<tr><td>").append(log.getKey()).append("</td><td>")
					.append(log.getValue().accessCount).append("</td><td>")
					.append(log.getValue().getAverageCost())
					.append("</td></tr>\n");
			totalCount += log.getValue().accessCount.get();
			totolCost += log.getValue().costTime.get();
		}
		buf.append("</table>");

		buf.append("<h3>Access Summary</h3>");
		buf.append("<br>Total Access Count: \n").append(totalCount);
		buf.append("<br>Total Cost Time: \n").append(
				format.format(totolCost / 1000000));
		if (totalCount != 0) {
			buf.append("<br>Average Execute Cost Time: \n").append(
					format.format(totolCost / totalCount / 1000000));
		}

		return buf.toString();
	}
}
