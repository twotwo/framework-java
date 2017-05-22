/**
 * QihooTest.java create at Apr 6, 2017 4:13:56 PM
 */
package com.li3huo.sdk.adapter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.li3huo.sdk.ChannelCode;
import com.li3huo.sdk.domain.AgentToken;
import com.li3huo.sdk.domain.Voucher;

/**
 * @ClassName: QihooTest
 * @Description: TODO
 * @author liyan
 * @date Apr 6, 2017 4:13:56 PM
 *
 */
public class QihooTest extends Adapter {
	String uri = "/api/PayNotify/360/500006/ ";
	String game_id = "500006";
	Validator v;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		v = ValidatorFactory.getValidator(game_id, ChannelCode.qihoo);
	}

	@Test
	public void check_token() {
		AgentToken token = new AgentToken();
		token.appid = "500006";
		token.channelId = ChannelCode.qihoo;
		token.channelData = new JSONObject();
		JSONObject data = new JSONObject();
		token.channelData.put("data", data);

		data.put("access_token", "2846383641ee9f7562b39ec3f998870bc1f6d8ac16dfc027cc");
		data.put("expires_in", "36000");
		data.put("server_code", "b");
		data.put("state", "test_state111");
		v.check_token(token);
		logger.debug("token = " + token.toJSONString());
		Assert.assertEquals(0, token.code);
	}

	@Test
	public void pay_notify() {

		String uri = "/api/PayNotify/QH/500006/";
		String params = "?order_id=1703163703999689041&app_key=c91a4fd40d51798d70cbf19b3c1bbc6b&product_id=111111111111111&amount=1&app_uid=2846383641&user_id=2846383641&sign_type=md5&gateway_flag=success&sign=4f7d7869c59b6b65cf6377280a21816b&sign_return=2a19db57b1da3021683673bb984fcc5d";

		Voucher voucher = new Voucher();
		voucher.channelId = ChannelCode.qihoo;
		voucher.appid = game_id;

		v.check_pay_notify(voucher, getContext(uri + params, null));

		logger.debug("voucher = " + voucher.toJSONString());
		Assert.assertTrue(voucher.code==0);
	}

	@Test
	public void pay_notify_100_times() {
		for (int i = 0; i < 100; i++) {
			pay_notify();
		}
	}

}
