/**
 * PayNotifyTest.java create at Mar 23, 2017 2:34:17 PM
 */
package com.li3huo.sdk.adapter;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.li3huo.sdk.tools.HttpUtil;

/**
 * @author liyan
 *
 */
public class PayNotifyTest {
	static final Logger logger = LogManager.getLogger(PayNotifyTest.class.getName());

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
	public void qihooPayNotify() throws IOException {
		String url = "";
		url = "http://54.222.142.17:8000/api/PayNotify/360/500006/";
//		url = "http://localhost:8000/api/PayNotify/360/500006/";
		String params = "?order_id=1703163703999689041&app_key=c91a4fd40d51798d70cbf19b3c1bbc6b&product_id=111111111111111&amount=1&app_uid=2846383641&user_id=2846383641&sign_type=md5&gateway_flag=success&sign=4f7d7869c59b6b65cf6377280a21816b&sign_return=2a19db57b1da3021683673bb984fcc5d";
		String response = HttpUtil.doPost(url+params);
		Assert.assertNotNull(response);
		logger.debug(response);
	}

}
