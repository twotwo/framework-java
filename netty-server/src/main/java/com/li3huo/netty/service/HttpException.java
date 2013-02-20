/**
 * Create at Jan 30, 2013
 */
package com.li3huo.netty.service;

/**
 * @author liyan
 *
 * Business Base Exception
 */
public class HttpException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6320880187062436611L;

	/**
	 * @param name
	 * @param ex
	 */
	public HttpException(String name, Throwable ex) {
		super(name, ex);
	}

	/**
	 * @param name
	 */
	public HttpException(String name) {
		super(name);
	}

	/**
	 * @param name
	 */
	public HttpException(Throwable name) {
		super(name);
	}

}
