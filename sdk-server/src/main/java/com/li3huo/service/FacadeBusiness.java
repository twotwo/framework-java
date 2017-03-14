/**
 * 
 */
package com.li3huo.service;

import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.li3huo.sdk.App;
import com.li3huo.sdk.auth.TokenInfo;
import com.li3huo.sdk.auth.TokenValidator;
import com.li3huo.sdk.notify.NotifyInfo;
import com.li3huo.sdk.notify.NotifyValidator;
import com.li3huo.sdk.notify.Voucher;

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
		logger.debug("dispatch by uri: " + uri);
		String gameId = StringUtils.substringBetween(uri, "/api/", "/");
		String gameName = App.getProperty(gameId+".name", "Unknown");
		logger.debug("process(): find game name [" + gameId + "] is " + gameName);
		
		logger.debug("headers: " + ctx.getHeaders());
		
		logger.debug( StringUtils.toEncodedString(request, Charset.forName("UTF-8")));
		
		//CP请求登录验证: https://<url>/api/<game_id>/LoginAuth/
		if (StringUtils.indexOf(uri, "/LoginAuth/") > 0) {
			TokenInfo bean = TokenInfo.parse(StringUtils.toEncodedString(request, Charset.forName("UTF-8")));
			TokenValidator.check_channel_sign(bean);
			logger.debug("LoginAuth: bean = " + bean.toJSONString());
			return bean.toJSONString();
		}
		
		//渠道通知支付结果：https://<url>/api/<game_id>/PayNotify/<channel_name>/
		if (StringUtils.indexOf(uri, "/PayNotify/") > 0) {
			String channelName = StringUtils.substringBetween(uri, "/PayNotify/", "/");
			logger.debug("PayNotify: channelName = " + channelName);
			NotifyInfo bean = NotifyInfo.parse(StringUtils.toEncodedString(request, Charset.forName("UTF-8")));
			logger.debug("PayNotify: bean = " + bean.toJSONString());
			NotifyValidator.check_channel_sign(bean);
			
			
			Voucher toGameInfo = new Voucher();
			toGameInfo.appid = gameId;
			toGameInfo.channel_name = channelName;
			toGameInfo.channel_order_id = bean.florderid;
			toGameInfo.game_order_id = bean.cporderid;
			toGameInfo.paid = bean.match;
			
			if(bean.match) {
				logger.debug("write to queue.");
			}
			
			return toGameInfo.toJSONString();

			
		}
		
		return "URI: " + uri + "\r\n" + ctx.getHeaders() + "\r\n" + fakeProcess(request);
	}

	private static String fakeProcess(byte[] request) {
		String info = StringUtils.toEncodedString(request, Charset.forName("UTF-8"));
		info += "Size: " + request.length+ "\r\nCONTENT: " + info;
		return info;
	}
}
