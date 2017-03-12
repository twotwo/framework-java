/**
 * 
 */
package com.li3huo.sdk.tools;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author liyan
 *
 */
public class MD5Test {
	static final Logger logger = LogManager.getLogger(MD5Test.class.getName());

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		String source = "eat your own dog food!\n宁愿做过了后悔,也不要错过了后悔！";
		String sign = "1e21f732fb9ef67892903fc74aee3f8b";

		
		String destStr = DigestUtils.md5Hex(source);
		logger.debug("destStr = " + destStr);
		Assert.assertEquals("Should be equal.", sign, destStr);
	}

}
