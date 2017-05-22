package com.li3huo.sdk;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.li3huo.sdk.domain.AgentOrder;
import com.li3huo.sdk.domain.AgentToken;
import com.li3huo.sdk.tools.HttpUtil;

/**
 * 
 * @author liyan
 *
 */
public class AppTest {

	static final Logger logger = LogManager.getLogger(AppTest.class.getName());

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		App.initConfig("conf/games-local.properties");

		// http://logging.apache.org/log4j/2.x/manual/customconfig.html#AddingToCurrent
		final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		final Configuration config = ctx.getConfiguration();

		AppenderRef ref = AppenderRef.createAppenderRef("STDOUT", null, null);
		AppenderRef[] refs = new AppenderRef[] { ref };
		@SuppressWarnings("deprecation")
		LoggerConfig loggerConfig = LoggerConfig.createLogger("true", Level.ERROR, "com.feiliu.sdk.tools.HttpUtil",
				"true", refs, null, config, null);
		loggerConfig.addAppender(config.getAppender("STDOUT"), null, null);
		config.addLogger("com.feiliu.sdk.tools.HttpUtil", loggerConfig);
		ctx.updateLoggers();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

//	@Test
//	public void testExecutor() {
//		Runnable job = new Runnable() {
//			@Override
//			public void run() {
//				try {
//					logger.debug("run");
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		};
//		for (int i = 0; i < 100; i++) {
//			App.execute(job, 0, 5, TimeUnit.MICROSECONDS);
//		}
//		logger.debug("done");
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		logger.debug("exit");
//	}

	@Test
	public void test() {

		String t_url = "";
		t_url = "http://54.222.142.17:8000/api/";
//		t_url = "http://localhost:8000/api/";
		// t_url = "http://211.159.148.170/api/";
		testLoginAuth(t_url);
		testSignOrder(t_url);
		testPayNotify(t_url, "500006");
	}

	/**
	 * 把conf/t_*.json的报文都发送给指定的登录服务
	 * 
	 * @param url
	 */
	public void testLoginAuth(String url) {
		String current = null;

		for (File file : FileUtils.listFiles(new File("conf/"), new WildcardFileFilter("t_*.json"), null)) {
			logger.debug("trying " + file.getName() + " ...");
			try {
				current = file.getName();
				String response = HttpUtil.doPost(url + "LoginAuth/",
						FileUtils.readFileToString(file, Charset.forName("UTF-8")));
				logger.debug("response=" + response);
				AgentToken token = AgentToken.parse(response);
				logger.debug("code=" + token.code);
				logger.info("trying {}, code= {}, msg = {}", file.getName(), token.code, token.msg);
				// Assert.assertEquals(0, token.code);
			} catch (IOException e) {
				logger.error("Exception at " + current + " " + e.getMessage());
			}
		}
	}

	/**
	 * 验单
	 * 
	 * @param url
	 */
	public void testSignOrder(String url) {
		String current = null;

		for (File file : FileUtils.listFiles(new File("conf/"), new WildcardFileFilter("o_*.json"), null)) {
			logger.debug("trying " + file.getName() + " ...");
			try {
				current = file.getName();
				String response = HttpUtil.doPost(url + "SignOrder/",
						FileUtils.readFileToString(file, Charset.forName("UTF-8")));
				logger.debug("response=" + response);
				AgentOrder bean = AgentOrder.parse(response);
				logger.debug("code=" + bean.code);
				logger.info("trying {}, code= {}, msg = {}", file.getName(), bean.code, bean.msg);
				// Assert.assertEquals(0, token.code);
			} catch (IOException e) {
				logger.error("Exception at " + current + " " + e.getMessage());
			}
		}
	}

	/**
	 * 模拟支付通知
	 * 
	 * @param url
	 */
	public void testPayNotify(String url, String appid) {
		String current = null;

		for (File file : FileUtils.listFiles(new File("conf/"), new WildcardFileFilter("p_*.json"), null)) {
			logger.debug("trying " + file.getName() + " ...");
			try {
				current = file.getName();
				String channel = StringUtils.substringBetween(current, "p_", ".json");
				// https://<url>/api/PayNotify/<channel_name>/<game_id>/
				String p_url = url + "PayNotify/" + channel + "/" + appid+ "/";
				String response = HttpUtil.doPost(p_url, FileUtils.readFileToString(file, Charset.forName("UTF-8")));
				logger.info("trying {}, resp = {}, url = {}",current, response, p_url);
			} catch (IOException e) {
				logger.error("Exception at " + current + " " + e.getMessage());
			}
		}
	}

}
