/**
 * Create at Jan 30, 2013
 */
package com.li3huo.business;

/**
 * @author liyan
 *
 */
public class HandleRequestException extends BusinessException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6582145633763566879L;
	private int actionId;
	
	public HandleRequestException addAction(int actionId) {
		this.actionId = actionId;
		return this;
	}
	
	public int getActionId() {
		return actionId;
	}

	/**
	 * @param name
	 * @param throwable
	 */
	public HandleRequestException(String name, Throwable throwable) {
		super(name, throwable);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param name
	 */
	public HandleRequestException(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param throwable
	 */
	public HandleRequestException(Throwable throwable) {
		super(throwable);
		// TODO Auto-generated constructor stub
	}
}
