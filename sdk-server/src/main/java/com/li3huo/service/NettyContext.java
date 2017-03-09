/**
 * 
 */
package com.li3huo.service;

import java.util.Map;
import java.util.Properties;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author liyan
 *
 */
public class NettyContext implements FacadeContext {

	/**
	 * HTTP Headers
	 */
	private Properties headers;
	private HttpRequest request;

	/**
	 * 
	 */
	public NettyContext(HttpRequest request) {
		this.request = request;
	}

	@Override
	public String getUri() {
		return request.uri();
	}

	@Override
	public Properties getHeaders() {
		if (null == headers) {
			HttpHeaders hh = request.headers();
			headers = new Properties();
			if (!hh.isEmpty()) {
				for (Map.Entry<String, String> h : hh) {
					String key = h.getKey();
					String value = h.getValue();
					headers.setProperty(key, value);
				}
			}
		}
		return headers;
	}

}
