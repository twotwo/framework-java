/**
 * Create at Jan 31, 2013
 */
package com.li3huo.netty.startup;

import org.apache.log4j.Logger;

import com.li3huo.netty.util.JSONTool;

/**
 * @author liyan
 *
 */
public class Config {
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Config [port=" + port + ", portBackend=" + portBackend + "]";
	}

	private static Logger log = Logger.getLogger(Config.class.getName());
	
	private int port = 8080;
	private int portBackend = 8005;

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the portBackend
	 */
	public int getPortBackend() {
		return portBackend;
	}

	/**
	 * @param portBackend the portBackend to set
	 */
	public void setPortBackend(int portBackend) {
		this.portBackend = portBackend;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Config config = new Config();
		String jsonString = JSONTool.toJSONString(config);
		log.info("encode: "+jsonString);
		
		jsonString= "{\"port\":80,\"portBackend\":8005}";
		config = JSONTool.parseString2Object(jsonString, Config.class);
		log.info("decode: "+config);
	}

}
