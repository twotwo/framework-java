/**
 * 
 */
package com.li3huo.sdk.adapter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.li3huo.sdk.ChannelCode;
import com.li3huo.sdk.domain.AgentToken;
import com.li3huo.sdk.domain.Voucher;
import com.li3huo.sdk.tools.HttpUtil;
import com.li3huo.service.FacadeContext;

/**
 * <a href="http://172.16.100.90/confluence/display/FUSDK/meizu_flyme">业务逻辑</a>
 * 
 * @author liyan
 *
 */
public class Validator_Qihoo extends Validator {

	static final Logger logger = LogManager.getLogger(Validator_Qihoo.class.getName());

	// String sessionServer = "https://openapi.360.cn/oauth2/access_token";
	String sessionServer = "https://openapi.360.cn/user/me.json";
	String tradeServer = "http://msdk.mobilem.360.cn/pay/order_verify.json";
	HashMap<String, String> header = new HashMap<>();

	/**
	 * 根据游戏标识去加载对应渠道的参数
	 * 
	 * @param game_id
	 *            对接游戏标识
	 */
	public Validator_Qihoo(String game_id) {
		super(game_id, ChannelCode.qihoo);
		logger.debug("init: sessionServer = " + sessionServer);
		header.put("Content-Type", "application/x-www-form-urlencoded");
	}

	/**
	 * 验证渠道登录Token
	 * 
	 * @param bean
	 * @throws Exception
	 */
	@Override
	public void check_token(AgentToken token) {
		// channel token
		String data = "access_token=" + token.channelData.getJSONObject("data").getString("access_token");
		// sessionServer +
		logger.debug("check_token() channel[" + ChannelCode.qihoo + "] data =  " + data);

		try {

			// 获取接口返回结果
			String result;
			result = HttpUtil.doGet(sessionServer, data, header);

			logger.debug("[" + super.channel_name + "] Response: " + result);

			JSONObject json = JSONObject.parseObject(result);

			// 不存在error_code说明成功： 如果里面包含有“error_code”参数，则认为验证失败，否则成功
			// {"id":"2846383641","name":"GV170314104325","avatar":"http://p1.qhmsg.com/dm/48_48_100/t00df551a583a87f4e9.jpg?f=c91a4fd40d51798d70cbf19b3c1bbc6b"}
			if (!json.containsKey("error_code")) {

				token.userId = json.getString("id");
				token.certified = true;
				token.code = 0;
				token.msg = "";
				logger.debug("check_channel_sign(): sign is valid.");
			} else {
				token.code = json.getInteger("error_code");
				token.msg = json.getString("error");
			}
		} catch (UnsupportedEncodingException e) {
			token.code = 1;
			token.msg = e.getMessage();
		} catch (IOException e) {
			token.code = 2;
			token.msg = e.getMessage();
		}

	}

	private final String FAILURE = "verify failed";
	private final String SUCCESS = "ok";

	@Override
	public void check_pay_notify(Voucher voucher, FacadeContext ctx) {
		/** 摘要的原文 */
		StringBuffer sb = new StringBuffer();
		// 升序键顺序
		Map<String, String> keyMap = new TreeMap<String, String>();

		for (Entry<String, String[]> p : ctx.getParameterMap().entrySet()) {

			String key = p.getKey();
			if ("sign".equals(key) || "sign_return".equals(key)) {
				continue;
			}
			String value = StringUtils.join(p.getValue());
			keyMap.put(key, value);
		}

		for (Entry<String, String> p : keyMap.entrySet()) {

			String value = StringUtils.join(p.getValue());
			if (StringUtils.isBlank(value) || ("0".equals(value))) {
				continue;
			}
			sb.append(value).append("#");
		}

		sb.append(getProperty("appsecret", "unknown"));
		logger.debug("check_pay_notify(): src = " + sb.toString());
		String destStr = DigestUtils.md5Hex(sb.toString());
		logger.debug("check_pay_notify(): dest = " + destStr);
		keyMap.put("sign", destStr);

		try {

			// 获取接口返回结果
			String result;
			// tradeServer
			result = HttpUtil.doGet(tradeServer, HttpUtil.getParameterString(keyMap), header);

			logger.debug("[" + super.channel_name + "] Response: " + result);

			JSONObject json = JSONObject.parseObject(result);

			Boolean verified = json.getString("ret").equals("verified");

			if (verified) {

				// 订单号、金额等信息。360不返回金额
				voucher.orderid = ctx.getParameter("order_id");
				voucher.userId = ctx.getParameter("user_id");
				voucher.pay_status = true; // 360没有支付成功状态标记
				voucher.code = 0;
				/** write voucher file */
				voucher.saveToFile(voucher_dir, logger);
				voucher.response_to_channel = SUCCESS;
			} else {
				voucher.code = 4;
				voucher.msg = json.getString("ret");
				voucher.response_to_channel = FAILURE;
			}
		} catch (UnsupportedEncodingException e) {
			voucher.code = 1;
			voucher.msg = e.getMessage();
		} catch (IOException e) {
			voucher.code = 2;
			voucher.msg = e.getMessage();
		}

	}
}
