/**
 * Create at Jan 24, 2013
 */
package com.li3huo.netty.service.snapshot;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;

/**
 * @author liyan
 * 
 */
public class SnapshotService {
	private static Map<String, AccessStatics> accessStatics = new HashMap<String, AccessStatics>();
	DecimalFormat format = new DecimalFormat("#,##0.### ms");

	public String getAccessKey(HttpRequest request) {
		return HttpHeaders.getHost(request, "") + request.getUri();
	}

	/**
	 * log for access uri and cost time
	 * 
	 * @param request
	 * @param costNanoTime
	 * 
	 * @deprecated
	 */
	public synchronized void logAccess(HttpRequest request, long costNanoTime) {
		String key = getAccessKey(request);
		if (null == accessStatics.get(key)) {
			accessStatics.put(key, new AccessStatics(key));
		}
		accessStatics.get(key).logAccess(costNanoTime);
	}
	
	private SumObjectMap accessAndCosttimebyUri = new SumObjectMap();
	private SumObjectMap processStatus = new SumObjectMap();
	private SumObjectMap accessByIP = new SumObjectMap();
	
	public void addMessageWatch(MessageWatch watch) {
		String key = watch.getRequestUri();
		accessAndCosttimebyUri.add(key, 1, watch.getAliveTime());
		
		key = ""+watch.getResponseStatus();
		processStatus.add(key, 1, watch.getAliveTime(MessageWatch.State_Work));
		
		key = watch.getRemoteIP();
		accessByIP.add(key, 1, watch.getAliveTime());
	}

	public String getAccessLog() {
		StringBuffer buf = new StringBuffer();

		long totalCount = 0, totolCost = 0;

		buf.append("<h3>Access Details</h3>")
				.append("<table border=\"1\">")
				.append("<tr><td><b>URI</b>\t</td><td><b>Count</b></td><td><b>Average Cost(ms)</b></td></tr>\n");
		for (Map.Entry<String, AccessStatics> log : accessStatics.entrySet()) {
			buf.append("<tr><td>").append(log.getKey()).append("</td><td>")
					.append(log.getValue().getAccessCount()).append("</td><td>")
					.append(formatLong(log.getValue().getAverageCost()))
					.append("</td></tr>\n");
			totalCount += log.getValue().getAccessCount().get();
			totolCost += log.getValue().getCostTime().get();
		}
		buf.append("</table>");

		buf.append("<h3>Access Summary</h3>");
		buf.append("<br>Total Access Count: \n").append(totalCount);
		buf.append("<br>Total Cost Time: \n").append(
				formatLong(totolCost / 1000000));
		if (totalCount != 0) {
			buf.append("<br>Average Execute Cost Time: \n").append(
					formatLong(totolCost / totalCount / 1000000));
		}

		return buf.toString();
	}
	
	private String formatLong(long number) {
		return format.format(number);
	}
}
