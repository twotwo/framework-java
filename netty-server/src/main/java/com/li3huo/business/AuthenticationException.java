/**
 * Create at Jan 30, 2013
 */
package com.li3huo.business;

/**
 * @author liyan
 *
 */
public class AuthenticationException extends BusinessException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 752684810930126164L;

	/**
	 * @param name
	 * @param throwable
	 */
	public AuthenticationException(String name, Throwable throwable) {
		super(name, throwable);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param name
	 */
	public AuthenticationException(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param throwable
	 */
	public AuthenticationException(Throwable throwable) {
		super(throwable);
		// TODO Auto-generated constructor stub
	}


}
