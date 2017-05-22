package com.li3huo.sdk;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.li3huo.sdk.domain.AgentKey;
import com.li3huo.sdk.tools.HttpUtil;

public class PayKeyTest {

	static final Logger logger = LogManager.getLogger(PayKeyTest.class.getName());

	@Test
	public void test() throws IOException {
		String t_url = "";
		t_url = "http://54.222.142.17:8000/api/GetKey/";
//		t_url = "http://localhost:8000/api/GetKey/";

		AgentKey key = new AgentKey();
		key.channelId = "huawei";
//		key.channelId = "coolpad";
		key.appid = "500006";
		key.keys = new JSONObject();
		key.keys.put("param", new String[] { "paykey", "BuoyPrivateKey" });
		logger.debug("request JSON=" + key.toJSONString());
		String response = HttpUtil.doPost(t_url, key.toJSONString());
		key = AgentKey.parse(response);
		logger.debug("code=" + key.code);
		Assert.assertEquals(0, key.code);
		logger.debug("response JSON=" + key.toJSONString());
	}

}
