/**
 * 
 */
package com.li3huo.service;

import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.li3huo.sdk.App;
import com.li3huo.sdk.adapter.Validator;
import com.li3huo.sdk.adapter.ValidatorFactory;
import com.li3huo.sdk.domain.AgentKey;
import com.li3huo.sdk.domain.AgentOrder;
import com.li3huo.sdk.domain.AgentToken;
import com.li3huo.sdk.domain.Voucher;

/**
 * @author liyan
 *
 */
public class FacadeBusiness {
	static final Logger logger = LogManager.getLogger(FacadeBusiness.class.getName());

	/**
	 * 隔离Netty逻辑，转入纯粹的业务逻辑处理
	 * 
	 * 目的：HTTP服务对FacadeBusiness来说是透明的。不做任何修改是可以放到Tomcat下的(需要实现TomcatContext)
	 * 
	 * @param ctx
	 * @param request
	 * @return
	 */
	public static String process(FacadeContext ctx) {

		String uri = ctx.getUri();
		// logger.debug("http method: " + ctx.getHttpMethod());
		// logger.debug("http headers: " + ctx.getHeaders());
		// logger.debug("http parameters: " + ctx.getParameters());

		/** 路由逻辑 */
		String method = StringUtils.substringBetween(uri, "/api/", "/");
		/** 服务开启debug模式的时候把所有请求打出来 */
		if (App.getProperty("agent.debug", "false").equalsIgnoreCase("true")) {
			logger.debug("=== Distribution Center Method = [" + method + "] [" + ctx.getRemoteAddr() + "] URI = [" + uri
					+ "] PARAMS = [" + ctx.getParameters() + "] REQ = ["
					+ StringUtils.toEncodedString(ctx.getInputStreamArray(), Charset.forName("UTF-8")) + "]");
		}

		/** CP请求登录验证: https://<url>/api/LoginAuth/ */
		if (StringUtils.indexOf(uri, "/LoginAuth/") > 0) {
			byte[] request = ctx.getInputStreamArray();
			AgentToken bean = AgentToken.parse(StringUtils.toEncodedString(request, Charset.forName("UTF-8")));
			String gameId = bean.appid;
			String gameName = App.getProperty(gameId + ".name", "Unknown");
			String channelName = bean.channelId;
			/** LoginAuth Request */
			logger.debug("[" + ctx.getRemoteAddr() + "] [" + gameName + "] " + gameId + ".LoginAuth." + channelName
					+ ".request JSON=" + bean.toJSONString());
			/** 根据游戏ID和渠道名称(客户端要求定义为channelId)获取具体验证类 */
			Validator v = ValidatorFactory.getValidator(gameId, bean.channelId);
			v.check_token(bean);
			/** LoginAuth Response */
			logger.debug("[" + ctx.getRemoteAddr() + "] [" + gameName + "] " + gameId + ".LoginAuth." + channelName
					+ ".response JSON=" + bean.toJSONString());
			/** 记录业务处理结果：状态码+状态消息 */
			ctx.setStatus(channelName, bean.code, bean.msg);
			return bean.toJSONString();
		}

		// 给订单签名：https://<url>/api/SignOrder/
		if (StringUtils.indexOf(uri, "/SignOrder/") > 0) {
			byte[] request = ctx.getInputStreamArray();
			AgentOrder bean = AgentOrder.parse(StringUtils.toEncodedString(request, Charset.forName("UTF-8")));
			String gameId = bean.appid;
			String channelName = bean.channelId;
			logger.debug("[" + ctx.getRemoteAddr() + "] " + gameId + ".SignOrder." + channelName + ".request JSON="
					+ bean.toJSONString());
			Validator v = ValidatorFactory.getValidator(gameId, bean.channelId);
			v.sign_order(bean);
			/** SignOrder Response */
			logger.debug("[" + ctx.getRemoteAddr() + "] " + gameId + ".SignOrder." + channelName + ".response JSON="
					+ bean.toJSONString());
			/** 记录业务处理结果：状态码+状态消息 */
			ctx.setStatus(channelName, bean.code, bean.msg);
			return bean.toJSONString();
		}

		// 渠道通知支付结果：https://<url>/api/PayNotify/<channel_name>/<game_id>/
		if (StringUtils.indexOf(uri, "/PayNotify/") > 0) {

			String channelName = StringUtils.substringBetween(uri, "/PayNotify/", "/");
			String gameId = StringUtils.substringBetween(uri, channelName + "/", "/");
			String gameName = App.getProperty(gameId + ".name", "Unknown");
			logger.debug("process(): PayNotify." + channelName + "." + gameId + "." + method);

			logger.debug(gameName + " [" + method + " from " + ctx.getRemoteAddr() + "] " + gameId + ".PayNotify."
					+ channelName + " uri=" + ctx.getUri() + "; headers=" + ctx.getHeaders() + "; input="
					+ StringUtils.toEncodedString(ctx.getInputStreamArray(), Charset.forName("UTF-8")));

			Voucher voucher = new Voucher();
			voucher.channelId = channelName;
			voucher.game_id = gameId;
			voucher.response = "init";
			logger.debug("[" + ctx.getRemoteAddr() + "] " + gameId + ".PayNotify." + channelName + ".create JSON="
					+ voucher.toJSONString());

			Validator v = ValidatorFactory.getValidator(voucher.game_id, voucher.channelId);
			v.check_pay_notify(voucher, ctx);
			/** PayNotify Response */
			logger.debug("[" + ctx.getRemoteAddr() + "] " + gameId + ".PayNotify." + channelName + ".update JSON="
					+ voucher.toJSONString());
			/** 记录业务处理结果：状态码+状态消息 */
			ctx.setStatus(channelName, voucher.code, voucher.msg);
			return voucher.response;
		}

		// SdkAgent来获取支付密钥：https://<url>/api/GetKey/
		if (StringUtils.indexOf(uri, "/GetKey/") > 0) {
			byte[] request = ctx.getInputStreamArray();
			AgentKey bean = AgentKey.parse(StringUtils.toEncodedString(request, Charset.forName("UTF-8")));
			JSONArray keys = bean.keys.getJSONArray("param");
			if (null == keys || keys.size() == 0) {
				bean.code = 404;
				bean.msg = "param is null";
			} else {

				boolean foundAll = true;
				for (int i = 0; i < keys.size(); i++) {
					// 只返回带.public.标记的字段
					// 500006.channel.huawei.public.paykey
					String key = StringUtils.lowerCase(keys.getString(i));
					String value = App.getProperty(bean.appid + ".channel." + bean.channelId + ".public." + key, null);
					if (null == value) {
						bean.code = 404;
						bean.msg = "not a valid key";
						foundAll = false;
						bean.keys.put(keys.getString(i), "not a valid key");
						break;
					}
					bean.keys.put(keys.getString(i), value);
				}
				if (foundAll)
					bean.code = 0;
			}
			return bean.toJSONString();
		}

		return fakeProcess(ctx);
	}

	private static String fakeProcess(FacadeContext ctx) {
		byte[] request = ctx.getInputStreamArray();
		/** Buffer that stores response info */
		StringBuilder buf = new StringBuilder();
		buf.append("URI: ").append(ctx.getUri()).append("\r\n");
		buf.append("Headers: ").append(ctx.getHeaders()).append("\r\n");
		// buf.append("Params:
		// ").append(ctx.getParametersString()).append("\r\n");
		buf.append("CONTENT: ");
		buf.append(StringUtils.toEncodedString(request, Charset.forName("UTF-8")));
		buf.append("\r\n");

		return buf.toString();
	}
}
