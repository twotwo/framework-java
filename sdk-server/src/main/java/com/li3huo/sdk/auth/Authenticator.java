/**
 * 
 */
package com.li3huo.sdk.auth;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.li3huo.sdk.App;
import com.li3huo.sdk.ChannelCode;
import com.li3huo.sdk.adapter.Validator;
import com.li3huo.sdk.adapter.ValidatorFactory;
import com.li3huo.sdk.adapter.Validator_Meizu;
import com.li3huo.sdk.adapter.Validator_UC;
import com.li3huo.sdk.adapter.qihoo.Pay;
import com.li3huo.sdk.adapter.qihoo.PayAppDemo;
import com.li3huo.sdk.adapter.qihoo.PayAppInterface;
import com.li3huo.sdk.adapter.qihoo.QException;
import com.li3huo.sdk.adapter.qihoo.QOAuth2;
import com.li3huo.service.FacadeContext;

/**
 * Authenticator Adaptor on Channels
 * 
 * @author liyan
 *
 */
public class Authenticator {

	static final Logger logger = LogManager.getLogger(Authenticator.class.getName());

	/**
	 * 认证 渠道login token 验证通过后返回渠道ID/UserID/认证成功标记
	 * 
	 * @param token:
	 *            与 SDK-Agent 约定的格式
	 */
	public static void check_login_token(AgentToken token) {

		// 360 的逻辑后续优化
		if (ChannelCode.qihoo.equalsIgnoreCase(token.channelId)) {
			try {
				QOAuth2 auth = new QOAuth2(App.getProperty(token.appid + ".channel.360.appkey", ""),
						App.getProperty(token.appid + ".channel.360.appsecret", ""), "basic");
				// channel token
				JSONObject data = token.channelData.getJSONObject("data");
				JSONObject _token = auth.userMe(data.getString("access_token"));
				logger.debug("check_login_token() channel[" + ChannelCode.qihoo + "] return " + _token);

				// {"id":"2846383641","name":"GV170314104325","avatar":"http://p1.qhmsg.com/dm/48_48_100/t00df551a583a87f4e9.jpg?f=c91a4fd40d51798d70cbf19b3c1bbc6b"}
				token.userId = _token.getString("id");
				token.certified = true;
			} catch (QException e) {
				e.printStackTrace();
				// {"error":"access token不可用（OAuth2）","error_code":"4010201"}
				logger.error("check_login_token(): Channel " + ChannelCode.qihoo + " " + e.getMessage());
				token.channelData.put("error_code", e.getCode());
				token.channelData.put("error", e.getMessage());
			}

		}
		/**
		 * FL 飞流 360 UC BD 百度 Vivo HW 华为 XM 小米 MZ 魅族
		 * 
		 * 
		 */
		Validator v = ValidatorFactory.getValidator(token.appid, token.channelId);
		v.check_token(token);
		logger.debug("response\n" + token.toJSONString());

	}

	/**
	 * 订单验签
	 * 
	 * @param order:
	 *            与 SDK-Agent 约定的格式
	 */
	public static void sign_order(AgentOrder order) {
		if (ChannelCode.uc.equalsIgnoreCase(order.channelId)) {
			Validator_UC vdt = new Validator_UC(order.appid);
			try {
				vdt.sign_order(order);
			} catch (Exception e) {
				e.printStackTrace();
			}
			logger.debug("ucSignOrder(): response\n" + order.toJSONString());
		}

		if (ChannelCode.meizu.equalsIgnoreCase(order.channelId)) {
			Validator_Meizu vdt = new Validator_Meizu(order.appid);
			try {
				vdt.sign_order(order);
			} catch (Exception e) {
				e.printStackTrace();
			}
			logger.debug("MeizuSignOrder(): response\n" + order.toJSONString());
		}
	}

	/**
	 * Authenticator for Channel Payment Notification
	 */

	public static void certify_pay_notification(Voucher voucher, FacadeContext ctx) {
		logger.debug("certify_pay_notification.channel=" + voucher.channel_name);
		// logger.debug(ctx.getParameters());
		if (ChannelCode.qihoo.equalsIgnoreCase(voucher.channel_name)) {
			try {
				PayAppInterface payApp = new PayAppDemo(App.getProperty(voucher.game_id + ".channel.360.appkey", ""),
						App.getProperty(voucher.game_id + ".channel.360.appsecret", ""));
				Pay pay = new Pay(payApp);
				HashMap<String, String> map = new HashMap<String, String>();
				for (String key : ctx.getParameterMap().keySet()) {
					map.put(key, ctx.getParameter(key));
				}
				voucher.game_order_id = ctx.getParameter("order_id");
				voucher.response = pay.processRequest(map);
				if ("ok".equals(voucher.response)) {
					voucher.certified = true;
				}
				logger.info("certify_pay_notification(): " + ChannelCode.qihoo + " " + voucher.toJSONString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// NotifyInfo bean = NotifyInfo.parse(StringUtils.toEncodedString(request,
	// Charset.forName("UTF-8")));
	// logger.debug("PayNotify: bean = " + bean.toJSONString());
	// NotifyValidator.check_channel_sign(bean);
	//
	// Voucher toGameInfo = new Voucher();
	// toGameInfo.appid = gameId;
	// toGameInfo.channel_name = channelName;
	// toGameInfo.channel_order_id = bean.florderid;
	// toGameInfo.game_order_id = bean.cporderid;
	// toGameInfo.paid = bean.match;
	//
	// if (bean.match) {
	// logger.debug("write to queue.");
	// }
	//
	// return toGameInfo.toJSONString();

}
