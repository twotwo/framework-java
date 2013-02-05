/**
 * 
 */
package com.li3huo.netty;

import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.li3huo.netty.startup.HttpServer;
import com.li3huo.netty.startup.Server;
import com.li3huo.netty.util.HttpTool;

/**
 * @author liyan
 * 
 */
public class HttpServerTest {
	
	private static Logger log = Logger.getLogger(HttpServerTest.class.getName());
	static Server server;
	
	static final String urlConsole = "http://localhost:8005/console";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		server = new HttpServer();
		server.start();
	}

	@AfterClass
	public static void releaseResource() throws Exception {
		server.stop();
	}
	
	@Test
	public void getStatus() throws Exception {
		assertNotNull(server);
		
		Properties param = new Properties();
		log.info(HttpTool.doGetRequest(urlConsole, param));
		param.setProperty("action", "status");
		for (int i = 0; i < 10; i++) {
			log.info(HttpTool.doGetRequest(urlConsole, param));
		}
	}

	
	@Test
	public void runThread() throws Exception {
		assertNotNull(server);
		for (int i = 0; i < 10; i++) {
		}
	}
}
