/**
 * URLCodecTest.java create at Mar 23, 2017 11:27:04 AM
 */
package com.li3huo.sdk.tools;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.commons.lang3.time.StopWatch;
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
public class URLCodecTest {
	static final Logger logger = LogManager.getLogger(URLCodecTest.class.getName());

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

	// URL Encoded
	String dest = "DjOF1YCnVyILgt3+iH+FUsprZPweVP3ZoFomuDT4CzM9OiJSMMz9n2cSyuLSpWlv/uZwKL0RC4J3C+vhbRSUSq3qmwfv/LQJs0e+k7lTpZbSLzPYUrcY63WsFlHOKwyWJgmKnAJbkvGAsk1425DmsQfsSmT23OYUc8vpnAwp+rU=";
	// src code
	String src = "WCpwt3lNDFvVDn0G02EMU28%2Fw7tFjblJHUR9rnrKlhREC%2FC2gXaNP9LNVFnLOk2Y3XiC%2BjXezQV4RzFBpyFV4QEDZyvj%2FQkurA5JBV17nhHq8yuiqsT4M4eQV85KChM6E1D4orEmIfB9fVby4zPppA6WIJip53LxPp0QMvl6Kis%3D";

	@Test
	public void encode() throws UnsupportedEncodingException {
		StopWatch sw = new StopWatch();
		sw.start();
		logger.debug("encode() src " + src);
		logger.debug("encode() src to " + URLEncoder.encode(src, "utf-8"));
		logger.debug("encode() dest " + dest);
		logger.debug("decode() dest to " + URLDecoder.decode(src, "utf-8"));
		Assert.assertEquals(URLEncoder.encode(src, "utf-8"), dest);
		for (int i = 0; i < 10; i++) {
			URLEncoder.encode(src, "utf-8");
		}
		logger.debug("encode() used " + sw);
	}
	
	@Test
	public void decode() throws UnsupportedEncodingException {
		StopWatch sw = new StopWatch();
		sw.start();
		Assert.assertEquals(URLDecoder.decode(dest, "utf-8"), src);
		for (int i = 0; i < 10; i++) {
			URLDecoder.decode(dest, "utf-8");
		}
		logger.debug("decode() used " + sw);
	}

}
