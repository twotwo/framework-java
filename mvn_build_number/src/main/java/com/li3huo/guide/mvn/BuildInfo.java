/**
 * 
 */
package com.li3huo.guild.mvn;

import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

/**
 * @author liyan
 * 
 */
public class BuildInfo {

	// ------------------------------------------------------- Static Variables

	/**
	 * The server information String with which we identify ourselves.
	 */
	private static String serverVendor = null;

	/**
	 * The server built String.
	 */
	private static String serverBranch = null;

	/**
	 * The server's version number String.
	 */
	private static String serverBuildNumber = null;

	static {

		try {
			/**
			 * search "add buildnumber to META-INF/MANIFEST.mf" in pom.xml
			 * 
			 */
			InputStream is = BuildInfo.class.getResourceAsStream("/assemble/build.properties");
			Properties props = new Properties();
			props.load(is);
			is.close();
			// groupId in pom.xml
			serverVendor = props.getProperty("server.vendor");
			System.out.println("server.version: " + props.getProperty("server.version"));
			// SCM Branch info, ${scmBranch} in pom.xml
			serverBranch = props.getProperty("server.tag");
			// SCM revision info, ${buildNumber} in pom.xml
			serverBuildNumber = props.getProperty("server.buildNumber");
		} catch (Throwable t) {
			System.err.println(t);
		}
		if (serverVendor == null)
			serverVendor = "Netty Demo Server";
		if (serverBranch == null)
			serverBranch = "unknown";
		if (serverBuildNumber == null)
			serverBuildNumber = "unknown";

	}

	// --------------------------------------------------------- Public Methods

	/**
	 * Return the App's Vendor.
	 */
	public static String getServerVendor() {

		return (serverVendor);

	}

	/**
	 * Return the App's SCM Branch.
	 */
	public static String getServerBranch() {

		return (serverBranch);

	}

	/**
	 * Return the app's build number.
	 */
	public static String getServerBuildNumber() {

		return serverBuildNumber;

	}

	private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static void loadUptime(StringBuffer out) throws IOException {
		RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
		out.append("\nStartTime: ").append(formatter.format(new Date(bean.getStartTime())));

		long s = bean.getUptime() / 1000;
		out.append("\nUptime: ");
		// more than 1 day 3600*24 = 86400
		if (s > 86400) {
			out.append(s / 86400);
			if (s > 86400 * 2) {
				out.append(" days, ");
			} else {
				out.append(" day, ");
			}
		}
		int h = (int) (s % 86400);
		out.append(h / 3600).append(" hours, ");
		out.append((h % 3600) / 60).append(" minutes.");

		out.append("\n======System Properties\n");
		for (Map.Entry<String, String> entry : bean.getSystemProperties().entrySet()) {
			out.append("\n" + entry.getKey()).append(" : " + entry.getValue());
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		System.out.println("Server Vendor: " + getServerVendor());
		System.out.println("SCM Branch:   " + getServerBranch());
		System.out.println("Build Number:  " + getServerBuildNumber());

		if (args.length == 0) {
			return;
		}
		System.out.println("OS Name:        " + System.getProperty("os.name"));
		System.out.println("OS Version:     " + System.getProperty("os.version"));
		System.out.println("Architecture:   " + System.getProperty("os.arch"));
		System.out.println("JVM Version:    " + System.getProperty("java.runtime.version"));
		System.out.println("JVM Vendor:     " + System.getProperty("java.vm.vendor"));

		StringBuffer out = new StringBuffer();
		loadUptime(out);

		System.out.println(out);
	}

	public static void loadSystemInfo(StringBuffer out) throws IOException {
		OperatingSystemMXBean op = ManagementFactory.getOperatingSystemMXBean();
		out.append("Architecture: ").append(op.getArch());
		out.append("\nProcessors: ").append(String.valueOf(op.getAvailableProcessors()));
		out.append("\nSystem name: ").append(op.getName());
		out.append("\nSystem version: ").append(op.getVersion());
		out.append("\nLast minute load: ").append(String.valueOf(op.getSystemLoadAverage()));
	}

	public static void loadMemoryInfo(StringBuffer out) throws IOException {
		MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
		MemoryUsage heap = mem.getHeapMemoryUsage();
		out.append("Commit Memory: ").append(String.valueOf(heap.getCommitted() / 1000000));
		out.append("\nInit Memory: ").append(String.valueOf(heap.getInit() / 1000000));
		out.append("\nMax Memory: ").append(String.valueOf(heap.getMax() / 1000000));
		out.append("\nUsed Memory: ").append(String.valueOf(heap.getUsed() / 1000000));
		out.append("\n");
	}

	static class MyThreadInfoBean {

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
			cpuTimeMillis = (int) threadBean.getThreadCpuTime(info.getThreadId()) / (1000 * 1000);
		}

		public String toString() {
			return "Id=" + info.getThreadId() + " \"" + name + "\"";
		}

		public String getStatus() {
			StringBuffer sb = new StringBuffer();

			if ("WAITING".equals(info.getThreadState().toString())) {
				sb.append("\nThread State: WAITING on " + info.getLockInfo());
			} else {
				sb.append("\nThread State: " + info.getThreadState());
			}

			sb.append("\nBlocked Count:" + info.getBlockedCount());
			sb.append("\nLock Name:" + info.getLockName());
			sb.append("\nLock Owner Name:" + info.getLockOwnerName());

			sb.append("\nStackTrace(depth:" + info.getStackTrace().length + ")");
			for (StackTraceElement element : info.getStackTrace()) {
				sb.append("\n\t+" + element);
			}
			return sb.toString();
		}
	}

	public static void loadThreadInfo(StringBuffer out) throws IOException {
		ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
		out.append("Thread Count: " + threadBean.getThreadCount());
		out.append("\nTotal Started Thread Count: " + threadBean.getTotalStartedThreadCount());
		long[] threadIDs = threadBean.getAllThreadIds();
		// Get the ThreadInfo object for each threadID
		ThreadInfo[] threadDataset = threadBean.getThreadInfo(threadIDs, Integer.MAX_VALUE);

		TreeSet<MyThreadInfoBean> threadSet = new TreeSet<MyThreadInfoBean>(new Comparator<MyThreadInfoBean>() {

			public int compare(MyThreadInfoBean arg0, MyThreadInfoBean arg1) {
				int i = arg1.getCpuTimeMillis() - arg0.getCpuTimeMillis();
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
			out.append("\n" + info + ", CpuTime = " + info.getCpuTimeMillis() + "ms.");
			out.append(info.getStatus());
		}

		out.append("\n");
	}

}
