/**
 * FacadeProcessor.java create at Apr 8, 2017 3:04:28 PM
 */
package com.li3huo.service;

/**
 * @ClassName: FacadeProcessor
 * @Description: TODO
 * @author liyan
 * @date Apr 8, 2017 3:04:28 PM
 *
 */
public interface FacadeProcessor {
	/**
	 * 按照uri路由逻辑分发处理器
	 * 
	 * @param prefix
	 *            uri前缀
	 * @param ctx
	 * @return
	 */

	public String process(String prefix, FacadeContext ctx);
}
