package com.li3huo.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.li3huo.netty.startup.Config;
import com.li3huo.util.JSONTool;

public class JsonTest {
	
	/**
	 * Config is a javabean
	 */
	Config config;

	@Before
	public void setUp() throws Exception {
		config = null;
	}

	@Test
	public void checkDefaultValue() {
		
		String expected = "{\"port\":8080,\"portBackend\":8005}";
		
		config = new Config();
		String jsonString = JSONTool.toJSONString(config);
		
		assertEquals(expected, jsonString);
		
	}
	
	@Test
	public void checkParse() {
		String jsonString= "{\"port\":80,\"portBackend\":8005}";
		config = JSONTool.parseString2Object(jsonString, Config.class);
		assertNotNull(config);
		assertSame(80, config.getPort());
	}

}
