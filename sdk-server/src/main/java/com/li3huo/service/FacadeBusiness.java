/**
 * 
 */
package com.li3huo.service;

import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.li3huo.sdk.App;
import com.li3huo.sdk.auth.Authenticator;
import com.li3huo.sdk.auth.TokenInfo;
import com.li3huo.sdk.auth.Voucher;

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
	public static String process(FacadeContext ctx, byte[] request) {
		String uri = ctx.getUri();
//		logger.debug("http method: " + ctx.getHttpMethod());
//		logger.debug("http headers: " + ctx.getHeaders());
//		logger.debug("http parameters: " + ctx.getParameters());

		/** 路由逻辑 */
		logger.debug("dispatch by uri: " + uri);
		String gameId = StringUtils.substringBetween(uri, "/api/", "/");
		String gameName = App.getProperty(gameId + ".name", "Unknown");
		String method = StringUtils.substringBetween(uri, gameId + "/", "/");
		logger.debug("process(): route for [" + gameId + "]" + gameName + "." + method + "()\n"
				+ StringUtils.toEncodedString(request, Charset.forName("UTF-8")));

		// CP请求登录验证: https://<url>/api/<game_id>/LoginAuth/
		if (StringUtils.indexOf(uri, "/LoginAuth/") > 0) {
			TokenInfo bean = TokenInfo.parse(StringUtils.toEncodedString(request, Charset.forName("UTF-8")));
			Authenticator.check_login_token(bean);
			logger.debug("LoginAuth: response()\n" + bean.toJSONString());
			return bean.toJSONString();
		}

		// 渠道通知支付结果：https://<url>/api/<game_id>/PayNotify/<channel_name>/
		if (StringUtils.indexOf(uri, "/PayNotify/") > 0) {
			String channelName = StringUtils.substringBetween(uri, "/PayNotify/", "/");
			logger.debug("PayNotify: channelName = " + channelName);
			
			Voucher bean = new Voucher();
			bean.channel_name = channelName;
			bean.game_id = gameId;
			bean.response = "init";
			Authenticator.certify_pay_notification(bean, ctx);
			return bean.response;
		}

		return "URI: " + uri + "\r\n" + ctx.getHeaders() + "\r\n" + fakeProcess(request);
	}

	private static String fakeProcess(byte[] request) {
		String info = StringUtils.toEncodedString(request, Charset.forName("UTF-8"));
		info += "Size: " + request.length + "\r\nCONTENT: " + info;
		return info;
	}
}
