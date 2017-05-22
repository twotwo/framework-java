/**
 * GameNotifierTest.java create at Apr 6, 2017 11:50:51 AM
 */
package com.li3huo.sdk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import com.li3huo.sdk.notify.GameNotifier;

/**
 * @ClassName: GameNotifierTest
 * @Description: TODO
 * @author liyan
 * @date Apr 6, 2017 11:50:51 AM
 *
 */
public class GameNotifierTest {
	static final Logger logger = LogManager.getLogger(GameNotifierTest.class.getName());

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String file = "conf/games.properties";
		App.initConfig(file);
	}

//	@Test
//	public void formatDate() {
//		String backup_format = "yyyy-MM-dd";
//		String str = DateFormatUtils.format(new Date(), backup_format);
//		logger.debug("backup to " + str);
//	}

	@Test
	public void test() {
		GameNotifier ntf = new GameNotifier("500006");
		ntf.run();
	}

}
