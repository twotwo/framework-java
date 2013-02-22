/**
 * Create at Jan 30, 2013
 */
package com.li3huo.business;

/**
 * @author liyan
 *
 */
public class ParseRequestException extends BusinessException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1560956195193735415L;

	/**
	 * @param name
	 * @param throwable
	 */
	public ParseRequestException(String name, Throwable throwable) {
		super(name, throwable);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param name
	 */
	public ParseRequestException(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param throwable
	 */
	public ParseRequestException(Throwable throwable) {
		super(throwable);
		// TODO Auto-generated constructor stub
	}

}
