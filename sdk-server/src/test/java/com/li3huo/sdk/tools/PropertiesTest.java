package com.li3huo.sdk.tools;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author liyan
 *
 */
public class PropertiesTest {
	
	static final Logger logger = LogManager.getLogger(PropertiesTest.class.getName());
	
	static Properties games = new Properties();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		games.load(new FileInputStream("conf/games.properties"));
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		logger.debug("name = "+games.getProperty("500006.name"));
		logger.debug("debug = "+games.getProperty("500006.debug", games.getProperty("agent.debug")));
		Assert.assertEquals("全职高手",games.getProperty("500006.name"));
	}

}
