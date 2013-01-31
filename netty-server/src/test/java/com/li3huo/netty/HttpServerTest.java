/**
 * 
 */
package com.li3huo.netty;

import static org.junit.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.li3huo.netty.startup.HttpServer;
import com.li3huo.netty.startup.Server;

/**
 * @author liyan
 * 
 */
public class HttpServerTest {
	static Server server = new HttpServer();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		server.start();
	}

	@AfterClass
	public static void releaseResource() throws Exception {
		server.stop();
	}

	
	@Test
	public void runThread() throws Exception {
		assertNotNull(server);
		for (int i = 0; i < 10; i++) {
		}
	}
}
