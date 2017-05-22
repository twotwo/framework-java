/**
 * FeiliuTest.java create at Apr 5, 2017 7:03:54 PM
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
 * @ClassName: FeiliuTest
 * @Description: TODO
 * @author liyan
 * @date Apr 5, 2017 7:03:54 PM
 *
 */
public class FeiliuTest extends Adapter {
	String uri = "/api/PayNotify/FL/500006/ ";
	String game_id = "500006";
	Validator v;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		v = ValidatorFactory.getValidator(game_id, ChannelCode.feiliu);
	}

	@Test
	public void check_token() {
		AgentToken token = new AgentToken();
		token.appid = "500006";
		token.channelId = ChannelCode.feiliu;
		token.channelData = new JSONObject();
		token.channelData.put("sign",
				"kQAuC71kcVMZ80R76kehVLv+9de7fKjDgaN1EpBb1xlSQoGAIvxorXZ61GFKFP3TGiHpIzCbRYaBHQd5kbXnBz0prr+8B6elS7tURDCR5fMMHBaOyyHVlgmRilDDR47bukYbxRlRL/0t88P4DZvBiFOsvJ1bA02DUNvGBOV4jqBQQtDM4IfaDT0by+uO2Vu33ITbS9/GZRg9spfweI378LqnKCbY/FcYtofEs/JO9s7K64wd5Gjr2kaqGdK/x/4m4RNIbq50da8DZruO7n/CoKgqvArFIgn4B20dZjZnYVrLr2yMUX5xRjIlYiDhNKgMrp27m95WU3dIfPna25JRAQ==");
		token.channelData.put("timestamp", "1491474184");
		token.channelData.put("userId", "1134815");
		v.check_token(token);
		logger.debug("token = "+token.toJSONString());
		Assert.assertEquals(0, token.code);
	}

	@Test
	public void pay_notify() {
		JSONObject notify = new JSONObject();
		notify.put("amount", "1");
		notify.put("appId", "500006");
		notify.put("cpOrderId", "123456789");
		notify.put("flOrderId", "0b5b42d30f015b46316eff00000004");
		notify.put("goodsId", "皮肤");
		notify.put("groupId", "");
		notify.put("merPriv", "透传参数");
		notify.put("roleId", "123");
		notify.put("sign", "3c718e89b3be8496d9d6b97253fda647");
		notify.put("status", "0");
		notify.put("userId", "1158818");

		Voucher voucher = new Voucher();
		voucher.channelId = ChannelCode.feiliu;
		voucher.appid = game_id;

		v.check_pay_notify(voucher, getContext(uri, notify.toJSONString()));

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
