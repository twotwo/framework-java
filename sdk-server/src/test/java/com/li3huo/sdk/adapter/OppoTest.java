package com.li3huo.sdk.adapter;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.codec.binary.Base64;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class OppoTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void sign() throws UnsupportedEncodingException {
		String sign = "Dc6rVRPliB473gIEiZu0V6H%2FXa1esgbA0xnyqdZsxXZNV6gQn8Exa7vKxZkaY1uErTSmVVMLEwCdjGMewkE%2BVB1z8BcEu09hL7gQ%2Bl4VzmfqzfukAr4SXvVQMCpWKqcz5WYggxxOl%2FHe%2FJ5WJLLD1MgxhyG1syrakhPznFwD1yQ%3D";
		byte[] bytes = Base64.decodeBase64(URLDecoder.decode(sign, "utf-8"));
		Assert.assertEquals(128, bytes.length);
	}

}
