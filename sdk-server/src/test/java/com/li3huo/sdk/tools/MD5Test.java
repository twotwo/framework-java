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

	@Test
	public void MZ_Signiture() {
		logger.debug("中国 " + DigestUtils.md5Hex("中国"));
		Assert.assertEquals("方法验证", "c13dceabcb143acd6c9298265d618a9f", DigestUtils.md5Hex("中国"));

		String destStr = DigestUtils.md5Hex(
				"app_id=3161635&buy_amount=1&cp_order_id=222222222222&create_time=1490089327438&pay_type=0&product_body=皮肤&product_id=0&product_per_price=1&product_subject=购买皮肤&product_unit=&total_price=1&uid=138258487&user_info=透传参数:19KI1N9Hw5VXDyMhEXPlHJLFs9wQXbHc");
		logger.debug("Channel = " + destStr);
		Assert.assertEquals("魅族渠道加签", "f3678955fe6cd24afc1b860ea79360be", destStr);

		destStr = DigestUtils.md5Hex(
				"app_id=3161635&buy_amount=1&cp_order_id=222222222222&create_time=1490089327438&pay_type=0&product_body=%E7%9A%AE%E8%82%A4&product_id=0&product_per_price=1&product_subject=%E8%B4%AD%E4%B9%B0%E7%9A%AE%E8%82%A4&product_unit=&total_price=1&uid=138258487&user_info=%E9%80%8F%E4%BC%A0%E5%8F%82%E6%95%B0:19KI1N9Hw5VXDyMhEXPlHJLFs9wQXbHc");
		logger.debug("Agent = " + destStr);
		Assert.assertEquals("适配加签", "9d6fe747771b7510e9d1bb72659650a3", destStr);
	}

}
