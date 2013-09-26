/**
 * Create at Sep 25, 2013
 */
package com.li3huo.business;

import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.HttpRequest;

/**
 * @author liyan
 *
 */
public class MailLogic {
	
	static Logger logger = Logger.getLogger(MailLogic.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * User=suchao
	 */
	public static boolean authenticate(HttpRequest request) {
//		if(request.getUri().indexOf("User=suchao")>0) {
//			return false;
//		}
		return true;
	}
}
