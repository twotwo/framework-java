/**
 * Create at Jan 16, 2013
 */
package com.li3huo.netty.service;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;


/**
 * @author liyan
 *
 */
public class ConsoleHandler extends HttpRequestHandler {
	
	private final AtomicLong accessCount = new AtomicLong();
	
	private StringBuilder help = new StringBuilder();
	

	private Logger log;
	private ServiceContext context;

	public ConsoleHandler(int port, ServiceContext context) {
		super(port, context);
		this.log = Logger.getLogger("Server[" + port + "]");
		this.context = context;
		
		//create help hint
		help.append("Usage:\n");
		help.append("Actions\n");
		help.append("action=status\n");
	}
	
	/* (non-Javadoc)
	 * @see com.li3huo.netty.service.HttpRequestHandler#handleHttpRequest(org.jboss.netty.channel.MessageEvent, org.jboss.netty.handler.codec.http.HttpRequest)
	 */
	@Override
	public void handleHttpRequest(MessageEvent event, HttpRequest request) {

		accessCount.addAndGet(1);
		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
		Map<String, List<String>> params = queryStringDecoder.getParameters();
		
		List<String> actions = params.get("action");
		if(null == actions) {
			this.writeResponse(event, help.toString());
			return;
		}
		
		for(String action : actions) {
		
			if("status".equalsIgnoreCase(action)) {
				this.writeResponse(event, getStatusInfo());
				continue;
			}else if("stop".equalsIgnoreCase(action)) {
				this.writeResponse(event, "Access Count: "+getAccessCount());
				log.info("stoped by console, from " + event.getRemoteAddress());
				System.exit(0);
			}else {
				this.writeResponse(event, help.toString());
				return;
			}
		}
	}
	
	private String getStatusInfo() {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("<pre>");
		
		buffer.append("\nServer Access Count: "+getAccessCount());
		buffer.append("\nConsole Access Count: "+accessCount);
		
		buffer.append("\n\n====System Info");
		try {
			loadSystemInfo(buffer);
		} catch (IOException e) {
		}
		
		buffer.append("\n\n====Memory Info");
		try {
			loadMemoryInfo(buffer);
		} catch (IOException e) {
		}
		
		buffer.append("\n\n====Thread Info");
		try {
			showThreadInfo(buffer);
		} catch (IOException e) {
		}
		
		buffer.append("</pre>");
		
		return buffer.toString();
	}
	
	public void loadSystemInfo(StringBuffer out) throws IOException {
		OperatingSystemMXBean op = ManagementFactory.getOperatingSystemMXBean();
		out.append("Architecture: ").append(op.getArch());
		out.append("\nProcessors: ").append(
				String.valueOf(op.getAvailableProcessors()));
		out.append("\nSystem name: ").append(op.getName());
		out.append("\nSystem version: ").append(op.getVersion());
		out.append("\nLast minute load: ").append(
				String.valueOf(op.getSystemLoadAverage()));
	}
	
	private void loadMemoryInfo(StringBuffer out) throws IOException {
		MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
		MemoryUsage heap = mem.getHeapMemoryUsage();
		out.append("Commit Memory: ").append(
				String.valueOf(heap.getCommitted() / 1000000));
		out.append("\nInit Memory: ").append(
				String.valueOf(heap.getInit() / 1000000));
		out.append("\nMax Memory: ").append(
				String.valueOf(heap.getMax() / 1000000));
		out.append("\nUsed Memory: ").append(
				String.valueOf(heap.getUsed() / 1000000));
		out.append("\n");
	}
	
	class MyThreadInfoBean {

		private ThreadInfo info;
		private String name;
		private int cpuTimeMillis;

		/**
		 * @return the cpuTimeMillis
		 */
		public int getCpuTimeMillis() {
			return cpuTimeMillis;
		}

		ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();

		public MyThreadInfoBean(ThreadInfo info) {
			this.info = info;
			this.name = info.getThreadName();
			// 1 millisecond = 1000,000 nanoseconds
			cpuTimeMillis = (int) threadBean.getThreadCpuTime(info
					.getThreadId()) / (1000 * 1000);
		}

		public String toString() {
			return "Id="+info.getThreadId()+" \""+name+"\"";
		}
		
		public String getStatus() {
			StringBuffer sb = new StringBuffer();
			
			if("WAITING".equals(info.getThreadState().toString())) {
				sb.append("\nThread State: WAITING on "+info.getLockInfo());
			}else {
				sb.append("\nThread State: " + info.getThreadState());
			}
			
			sb.append("\nBlocked Count:" + info.getBlockedCount());
			sb.append("\nLock Name:" + info.getLockName());
			sb.append("\nLock Owner Name:" + info.getLockOwnerName());
			
			sb.append("\nStackTrace(depth:" + info.getStackTrace().length+")");
			for(StackTraceElement element:info.getStackTrace()) {
				sb.append("\n\t+" + element);
			}
			return sb.toString();
		}
	}	
	private void showThreadInfo(StringBuffer out) throws IOException {
		ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
		out.append("Thread Count: " + threadBean.getThreadCount());
		out.append("\nTotal Started Thread Count: "
				+ threadBean.getTotalStartedThreadCount());
		long[] threadIDs = threadBean.getAllThreadIds();
		// Get the ThreadInfo object for each threadID
		ThreadInfo[] threadDataset = threadBean.getThreadInfo(threadIDs, Integer.MAX_VALUE);

		TreeSet<MyThreadInfoBean> threadSet = new TreeSet<MyThreadInfoBean>(
				new Comparator<MyThreadInfoBean>() {

					public int compare(MyThreadInfoBean arg0,
							MyThreadInfoBean arg1) {
						int i = arg1.getCpuTimeMillis()
								- arg0.getCpuTimeMillis();
						if (i != 0)
							return i;
						else {
							return 1;
						}
					}

				});
		for (ThreadInfo threadInfo : threadDataset) {
			if (threadInfo != null) {
				// 1 second = 1000,000,000 nanoseconds
				// 1 millisecond = 1000,000 nanoseconds
				MyThreadInfoBean info = new MyThreadInfoBean(threadInfo);
				threadSet.add(info);

			}
		}

		for (MyThreadInfoBean info : threadSet) {
			out.append("\n-----------");
			out.append("\n" + info + ", CpuTime = " + info.getCpuTimeMillis()
					+ "ms.");
			out.append(info.getStatus() );
		}

		out.append("\n");
	}
	
}
