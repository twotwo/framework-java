package com.li3huo.sdk;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.li3huo.sdk.domain.AgentToken;
import com.li3huo.sdk.tools.HttpUtil;

/**
 * 
 * @author liyan
 *
 */
public class AppTest {

	static final Logger logger = LogManager.getLogger(AppTest.class.getName());

	static Properties games = new Properties();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		games.load(new FileInputStream("conf/games.properties"));
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		logger.debug(games.getProperty("500006.name"));
		Assert.assertEquals("全职高手", games.getProperty("500006.name"));

		String t_url = "";
		t_url = "http://54.222.142.17:8000/api/LoginAuth/";
		t_url = "http://localhost:8000/api/LoginAuth/";
		testLoginAuth(t_url);
	}

	public void testLoginAuth(String url) {

		for (File file : FileUtils.listFiles(new File("conf/"), new WildcardFileFilter("t_*.json"), null)) {
			logger.debug("trying "+file.getName()+" ...");
			try {
				String response = HttpUtil.doPost(url, FileUtils.readFileToString(file, Charset.forName("UTF-8")));
				logger.debug("json="+response);
				AgentToken token = AgentToken.parse(response);
				logger.debug("code="+token.code);
//				Assert.assertEquals(0, token.code);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
