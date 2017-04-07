/**
 * 
 */
package com.li3huo.sdk.domain;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.li3huo.sdk.App;

/**
 * Agent发给Game的已经支付凭据
 * 
 * @author liyan
 *
 */
public class Voucher extends Domain {
	/** User ID */
	public String userId;

	/** Game Role ID */
	public String roleId;

	/** Channel Order ID */
	public String channel_order_id;

	/** Game Order ID */
	public String orderid;

	/** Order Price */
	public String total_price;

	/**
	 * 渠道返回的支付结果
	 */
	public boolean pay_status;

	/**
	 * 给游戏的验签值
	 */
	public String sign;

	/**
	 * 给渠道的响应
	 */
	public String response_to_channel;

	/**
	 * 游戏服务器返回的响应
	 */
	// public String game_response;

	public static Voucher parse(String text) {
		return JSON.parseObject(text, Voucher.class);
	}

	public String toJSONString() {
		return JSON.toJSONString(this);
	}

	/**
	 * 生成sign，把voucher写到本地
	 * 
	 * @param voucher_dir
	 * @param logger
	 */
	public void saveToFile(String voucher_dir, Logger logger) {
		File file = new File(voucher_dir + "/" + App.getVoucherFileIndex() + "_" + this.channelId + ".json");
		signVoucher(logger);
		try {

			if (StringUtils.isNotBlank(App.getProperty("agent.debug", null))) {
				logger.debug("writing voucher to file[" + file.getAbsolutePath() + "]");
			}
			FileUtils.writeStringToFile(file, this.toJSONString(), Charset.forName("UTF-8"), false);
		} catch (IOException e) {
			logger.fatal("write voucher file[" + file.getAbsolutePath() + "] failed!\n" + this.toJSONString());
		}
	}

	/**
	 * 给Voucher加签名
	 */
	private void signVoucher(Logger logger) {
		StringBuffer sb = new StringBuffer();
		sb.append(appid).append("#");
		sb.append(channelId).append("#");
		if (StringUtils.isNotBlank(channel_order_id)) {
			sb.append(channel_order_id).append("#");
		}
		if (StringUtils.isNotBlank(orderid)) {
			sb.append(orderid).append("#");
		}
		if (StringUtils.isNotBlank(roleId)) {
			sb.append(roleId).append("#");
		}
		if (StringUtils.isNotBlank(userId)) {
			sb.append(userId).append("#");
		}
		// 500006.channel.fl.appsecret
		sb.append(App.getProperty(appid + ".channel.fl.appsecret", "unknown"));
		logger.debug("signVoucher(): src = " + sb.toString());
		sign = DigestUtils.md5Hex(sb.toString());
		logger.debug("signVoucher(): sign = " + sign);
	}

}
