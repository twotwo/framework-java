/**
 * Create at Jan 30, 2013
 */
package com.li3huo.netty.service;

/**
 * @author liyan
 *
 * Business Base Exception
 */
public class BusinessException extends Exception {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -6913932376307194146L;

	/**
	 * @param name
	 * @param ex
	 */
	public BusinessException(String name, Throwable ex) {
		super(name, ex);
	}

	/**
	 * @param name
	 */
	public BusinessException(String name) {
		super(name);
	}

	/**
	 * @param name
	 */
	public BusinessException(Throwable name) {
		super(name);
	}

}
