/**
 * 
 */
package com.li3huo.service;

import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.li3huo.sdk.auth.LoginBean;

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
		logger.debug("headers: " + ctx.getHeaders());
		
		if (StringUtils.indexOf(uri, "LoginAuth") > 0) {
			LoginBean bean = LoginBean.parse(StringUtils.toEncodedString(request, Charset.forName("UTF-8")));
			return bean.toJSONString();
		}
		
		return "URI: " + uri + "\r\n" + ctx.getHeaders() + "\r\n" + fakeProcess(request);
	}

	private static String fakeProcess(byte[] request) {
		String info = "Size: " + request.length;
		info += "\r\nCONTENT: " + StringUtils.toEncodedString(request, Charset.forName("UTF-8"));
		return info;

	}
}
