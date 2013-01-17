/**
 * 
 */
package com.li3huo.netty.util;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author liyan
 * 
 */
public class ServerInfo {

	// ------------------------------------------------------- Static Variables

	/**
	 * The server information String with which we identify ourselves.
	 */
	private static String serverInfo = null;

	/**
	 * The server built String.
	 */
	private static String serverBuilt = null;

	/**
	 * The server's version number String.
	 */
	private static String serverNumber = null;

	static {

		try {
			InputStream is = ServerInfo.class
					.getResourceAsStream("/com/li3huo/rnm/util/ServerInfo.properties");
			Properties props = new Properties();
			props.load(is);
			is.close();
			serverInfo = props.getProperty("server.info");
			serverBuilt = props.getProperty("server.built");
			serverNumber = props.getProperty("server.number");
		} catch (Throwable t) {
			;
		}
		if (serverInfo == null)
			serverInfo = "Netty Demo Server";
		if (serverBuilt == null)
			serverBuilt = "unknown";
		if (serverNumber == null)
			serverNumber = "1.0.0.0";

	}

	// --------------------------------------------------------- Public Methods

	/**
	 * Return the server identification for this version of Tomcat.
	 */
	public static String getServerInfo() {

		return (serverInfo);

	}

	/**
	 * Return the server built time for this version of Hesine Server.
	 */
	public static String getServerBuilt() {

		return (serverBuilt);

	}

	/**
	 * Return the server's version number.
	 */
	public static String getServerNumber() {

		return (serverNumber);

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Server version: " + getServerInfo());
		System.out.println("Server built:   " + getServerBuilt());
		System.out.println("Server number:  " + getServerNumber());
		System.out.println("OS Name:        " + System.getProperty("os.name"));
		System.out.println("OS Version:     "
				+ System.getProperty("os.version"));
		System.out.println("Architecture:   " + System.getProperty("os.arch"));
		System.out.println("JVM Version:    "
				+ System.getProperty("java.runtime.version"));
		System.out.println("JVM Vendor:     "
				+ System.getProperty("java.vm.vendor"));
	}

}
