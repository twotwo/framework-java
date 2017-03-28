package com.li3huo.sdk.adapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.li3huo.sdk.App;
import com.li3huo.sdk.ChannelCode;
import com.li3huo.sdk.domain.AgentToken;

public class ValidatorTest {

	public String appid = "500006";
	static final Logger logger = LogManager.getLogger(ValidatorTest.class.getName());

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String file = "conf/games.properties";
		App.initConfig(file);
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

	private AgentToken getAgentToken() {
		AgentToken token = new AgentToken();
		token.channelId = ChannelCode.xiaomi;
		token.appid = appid;
		token.channelData = new JSONObject();
		token.channelData.put("accessToken", "9iCMTeZyPGlNLQzG");
		token.channelData.put("userid", "200216675");
		logger.debug("xiaomi(): request\n" + token.toJSONString());
		return token;
	}

	@Test
	public void test() {
		Validator v = ValidatorFactory.getValidator(appid, ChannelCode.xiaomi);

		v.check_token(getAgentToken());
	}

}
