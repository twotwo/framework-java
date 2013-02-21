/**
 * Create at Feb 19, 2013
 */
package com.li3huo.netty.service.snapshot;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author liyan
 * 
 */
public class SumObjectMap {

	private String reportTitle;
	private Date reportStartDate = new Date();

	private Map<String, SumObject> map = new HashMap<String, SumObject>();

	public SumObjectMap(String title) {
		this.reportTitle = title;
	}

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

	DecimalFormat format = new DecimalFormat("#,##0.### ms");

	private String formatLong(long number) {
		return format.format(number);
	}

	/**
	 * 
	 * @param format
	 *            0-plantext, 1-html
	 * @return
	 */
	public String toString(int format) {

		// to html format
		StringBuffer buf = new StringBuffer();

		long totalCount = 0, totolCost = 0;

		buf.append(
				"<h3>" + this.reportTitle + "(start from "
						+ this.reportStartDate + ")</h3>")
				.append("<table border=\"1\">")
				.append("<tr><td><b>Key</b>\t</td><td><b>Count</b></td><td><b>Average Cost(ms)</b></td></tr>\n");
//		for (Entry<String, SumObject> record : map.entrySet()) {
		for(SumObject record: map.values()) {
			buf.append("<tr><td>").append(record.getKey());
			buf.append("</td><td>").append(record.accessCount);
			buf.append("</td><td>")
					.append(formatLong(record.costTime.get() / 1000000))
					.append("</td></tr>\n");
			totalCount += record.accessCount.get();
			totolCost += record.costTime.get();
		}

		buf.append("</table>");

		buf.append("<h3>Report Summary</h3>");
		buf.append("<br>Total Count: \n").append(totalCount);
		buf.append("<br>Total Cost Time: \n").append(
				formatLong(totolCost / 1000000));
		if (totalCount != 0) {
			buf.append("<br>Average Execute Cost Time: \n").append(
					formatLong(totolCost / totalCount / 1000000));
		}
		
		buf.append("<hr>\n");

		return buf.toString();
	}

}
