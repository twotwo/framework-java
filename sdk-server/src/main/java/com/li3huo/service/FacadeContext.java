/**
 * 
 */
package com.li3huo.service;

import java.util.Map;

/**
 * @author liyan
 *
 */
public interface FacadeContext {
	public static final String HTTP_GET = "GET";
	public static final String HTTP_POST = "POST";
	
	public String getHttpMethod();
	/**
	 * get HTTP URI
	 * 
	 * @return
	 */
	public String getUri();
	
	/**
	 * get HTTP Header by key
	 * 
	 * @return
	 */
	public String getHeader(String key);
	public String getHeaders();
	
	/**
	 * get HTTP Parameter by key
	 * @param key
	 * @return
	 */
	public String getParameter(String key);

//	public Properties getParameters();
	public Map<String, String[]> getParameterMap();
	
	public byte[] getInputStreamArray();
	
	public String getRemoteAddr();
}
