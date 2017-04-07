/**
 * GameNotifier.java create at Apr 5, 2017 6:29:46 PM
 */
package com.li3huo.sdk.notify;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.li3huo.sdk.App;
import com.li3huo.sdk.tools.HttpUtil;

/**
 * @ClassName: GameNotifier
 * @Description: 把支付结果通知给CP
 * @author liyan
 * @date Apr 5, 2017 6:29:46 PM
 *
 */
public class GameNotifier implements Runnable {
	static final Logger logger = LogManager.getLogger(GameNotifier.class.getName());
	/**
	 * 游戏名称
	 */
	private String game_name;
	/**
	 * 收据文件夹
	 */
	private String voucher_dir;
	private String failed_dir;

	/**
	 * CP接收支付通知的地址
	 */
	private String voucher_url;

	/**
	 * 
	 */
	public GameNotifier(String game_id) {
		this.game_name = App.getProperty(game_id + ".name", "未知游戏");
		this.voucher_dir = App.getProperty(game_id + ".game.pay.dir", null);
		this.voucher_url = App.getProperty(game_id + ".game.pay.url", null);
		if (null == voucher_dir || null == voucher_url) {
			logger.fatal(game_name + "[" + game_id + "]dir or url is null!");
			System.exit(-1);
		}
		File f = new File(voucher_dir);
		if (!f.isDirectory()) {
			logger.fatal(game_name + "[" + game_id + "]dir not exists!");
			System.exit(-1);
		}
		failed_dir = voucher_dir + "/failed";
	}

	private final String FAILURE = "failed";
	private final String SUCCESS = "success";

	@Override
	public void run() {
		int count = 0;
		while (count < 1000) {
			String current = null;
			for (File file : FileUtils.listFiles(new File(voucher_dir), new WildcardFileFilter("*.json"), null)) {
				logger.debug("trying " + file.getName() + " ...");
				try {
					current = file.getName();
					// Voucher v =
					// Voucher.parse(FileUtils.readFileToString(file,
					// Charset.forName("UTF-8")));
					JSONObject json = JSON.parseObject(FileUtils.readFileToString(file, Charset.forName("UTF-8")));

					String response = HttpUtil.doPost(voucher_url, json.toJSONString());
					logger.debug("response=" + response);
					if (SUCCESS.equals(response)) {
						logger.debug("notify ok, mv to backup");
						File destDir = new File(this.voucher_dir + DateFormatUtils.format(new Date(), "/yyyy-MM-dd"));
						if (StringUtils.isNotBlank(App.getProperty("agent.debug", null))) {
							logger.debug("backup to " + destDir.getAbsolutePath());
						}
						FileUtils.moveFileToDirectory(file, destDir, true);
					} else if (FAILURE.equals(response)) {
						int retry = json.getIntValue("retry");
						retry++;
						json.put("retry", retry);
						json.put("retry_reason", "response failure code");
						json.put("game_response", response);
						FileUtils.writeStringToFile(file, json.toJSONString(), Charset.forName("UTF-8"), false);
						if (retry > 3) {
							FileUtils.moveFileToDirectory(file, new File(failed_dir), true);
						}
					} else {
						int retry = json.getIntValue("retry");
						retry++;
						json.put("retry", retry);
						json.put("retry_reason", "unknown");
						json.put("game_response", response);
						FileUtils.writeStringToFile(file, json.toJSONString(), Charset.forName("UTF-8"), false);
						if (retry > 3) {
							FileUtils.moveFileToDirectory(file, new File(failed_dir), true);
						}
					}

					// Assert.assertEquals(0, token.code);
				} catch (IOException e) {
					logger.error("Exception at " + current + " " + e.getMessage());
				}
			}
			try {
				if (StringUtils.isNotBlank(App.getProperty("agent.debug", null))) {
					logger.debug("sleep a while for next send.");
				}
				Thread.sleep(10000);
			} catch (InterruptedException e) {
			}
		}
	}

}
