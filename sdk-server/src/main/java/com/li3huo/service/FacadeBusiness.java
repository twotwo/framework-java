/**
 * 
 */
package com.li3huo.service;

import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.li3huo.sdk.App;
import com.li3huo.sdk.auth.AgentOrder;
import com.li3huo.sdk.auth.AgentToken;
import com.li3huo.sdk.auth.Authenticator;
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
	public static String process(FacadeContext ctx) {
		String uri = ctx.getUri();
		// logger.debug("http method: " + ctx.getHttpMethod());
		// logger.debug("http headers: " + ctx.getHeaders());
		// logger.debug("http parameters: " + ctx.getParameters());

		/** 路由逻辑 */
		logger.debug("[" + ctx.getRemoteAddr() + "] dispatch by uri: " + uri);
		String method = StringUtils.substringBetween(uri, "/api/", "/");
		logger.debug("===access_info uri = "+ uri+"\nparams = ["+ctx.getParameters()+"]\nreq\n"+StringUtils.toEncodedString(ctx.getInputStreamArray(), Charset.forName("UTF-8")));

		// CP请求登录验证: https://<url>/api/LoginAuth/
		if (StringUtils.indexOf(uri, "/LoginAuth/") > 0) {
			byte[] request = ctx.getInputStreamArray();
			AgentToken bean = AgentToken.parse(StringUtils.toEncodedString(request, Charset.forName("UTF-8")));
			String gameId = bean.appid;
			String gameName = App.getProperty(gameId + ".name", "Unknown");
			logger.debug("LoginAuth:  [" + gameId + "]" + gameName + " channelName = " + bean.channelId);
			Authenticator.check_login_token(bean);
			logger.debug("LoginAuth: response()\n" + bean.toJSONString());
			return bean.toJSONString();
		}
		
		// 给订单签名：https://<url>/api/SignOrder/
		if (StringUtils.indexOf(uri, "/SignOrder/") > 0) {
			byte[] request = ctx.getInputStreamArray();
			AgentOrder bean = AgentOrder.parse(StringUtils.toEncodedString(request, Charset.forName("UTF-8")));
			String gameId = bean.appid;
			String gameName = App.getProperty(gameId + ".name", "Unknown");
			logger.debug("SignOrder:  [" + gameId + "]" + gameName + " channelName = " + bean.channelId);
			Authenticator.sign_order(bean);
			logger.debug("SignOrder: response()\n" + bean.toJSONString());
			return bean.toJSONString();
		}

		// 渠道通知支付结果：https://<url>/api/PayNotify/<channel_name>/<game_id>/
		if (StringUtils.indexOf(uri, "/PayNotify/") > 0) {

			String channelName = StringUtils.substringBetween(uri, "/PayNotify/", "/");
			String gameId = StringUtils.substringBetween(uri, channelName + "/", "/");
			String gameName = App.getProperty(gameId + ".name", "Unknown");
			logger.debug("process(): route for [" + gameId + "]" + gameName + "." + method);

			logger.debug("PayNotify:  [" + gameId + "]" + gameName + " channelName = " + channelName);

			Voucher bean = new Voucher();
			bean.channel_name = channelName;
			bean.game_id = gameId;
			bean.response = "init";
			Authenticator.certify_pay_notification(bean, ctx);
			return bean.response;
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
