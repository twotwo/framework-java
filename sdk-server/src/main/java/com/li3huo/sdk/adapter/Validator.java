/**
 * 
 */
package com.li3huo.sdk.adapter;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.li3huo.sdk.App;
import com.li3huo.sdk.domain.AgentOrder;
import com.li3huo.sdk.domain.AgentToken;
import com.li3huo.sdk.domain.Voucher;
import com.li3huo.service.FacadeContext;

/**
 * @author liyan
 *
 */
public abstract class Validator {
	static final Logger logger = LogManager.getLogger(Validator.class.getName());
	/** 游戏唯一标示 */
	String game_id;
	/** 渠道唯一标示 */
	String channel_name;
	/** 游戏订单生成位置 */
	String voucher_dir;

	/**
	 * @param game_id
	 * @param channel_name
	 */
	public Validator(String game_id, String channel_name) {
		this.game_id = game_id;
		this.channel_name = channel_name.toLowerCase();
		this.voucher_dir = App.getProperty(game_id + ".game.pay.dir", null);
		logger.debug(game_id + ".game.pay.dir");
		if (this.voucher_dir == null) {
			logger.fatal("[" + game_id + "] voucher dir is null!");
		}
		File f = new File(this.voucher_dir);
		if (!f.isDirectory()) {
			logger.fatal("[" + game_id + "] creating voucher dir " + this.voucher_dir + " status " + f.mkdirs());
		}
	}

	/**
	 * 获取 <game_id>.channel.huaw<channel_name>.key的值
	 * 
	 * @param key
	 * @param defValue
	 * @return 例如 500006.channel.huawei.appid的值
	 */
	String getProperty(String key, String defValue) {
		return App.getProperty(game_id + ".channel." + channel_name + "." + key, defValue);
	}

	/**
	 * 按照 key=value&key=value生成String, 对value不做任何转换
	 * 
	 * @param keyMap
	 * @return
	 */
	String linkString(Map<String, String> keyMap) {
		if (keyMap == null || keyMap.size() == 0) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		for (String key : keyMap.keySet()) {
			sb.append(key).append("=");
			sb.append(keyMap.get(key)).append("&");
		}

		return StringUtils.removeEnd(sb.toString(), "&");
	}

	/**
	 * 验证登录Token
	 * 
	 * @param bean
	 */
	public abstract void check_token(AgentToken bean);

	public void sign_order(AgentOrder order) {
		logger.error("game[" + order.appid + "], channel[" + order.channelId + "]: not impletent yet!");
	}

	/**
	 * 验证支付通知，并生成购买订单
	 * 
	 * @param voucher:
	 *            购买订单
	 * @param ctx
	 */
	public void check_pay_notify(Voucher voucher, FacadeContext ctx) {
		voucher.response_to_channel = "game[" + voucher.appid + "], channel[" + voucher.channel_order_id + "]: not impletent yet!";
		logger.error(voucher.response_to_channel);
	}
}
