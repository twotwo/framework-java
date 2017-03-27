/**
 * TimeLogger.java create at Mar 27, 2017 11:49:20 AM
 */
package com.li3huo.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

/**
 * @ClassName: TimeLogger
 * @Description: TODO
 * @author liyan
 * @date Mar 27, 2017 11:49:20 AM
 *
 */
public class TimeLogger {
	/** 处理时间记录 */
	StopWatch sw = new StopWatch();
	private StringBuffer swLog = new StringBuffer();

	public void logTime(String tag) {
		swLog.append(tag).append("=").append(sw.toString()).append("s\t");
	}

	public String getTimeLog() {
		return StringUtils.removeEnd(swLog.toString(), "\t");
	}
}
