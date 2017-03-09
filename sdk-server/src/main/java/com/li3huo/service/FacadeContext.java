/**
 * 
 */
package com.li3huo.service;

import java.util.Properties;

/**
 * @author liyan
 *
 */
public interface FacadeContext {
	/**
	 * get HTTP URI
	 * 
	 * @return
	 */
	public String getUri();
	
	/**
	 * get HTTP Headers
	 * 
	 * @return
	 */
	public Properties getHeaders();
}
