/**
 * 
 */
package com.li3huo.service;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

/**
 * @author liyan
 *
 */
public class NettyContext implements FacadeContext {

	/**
	 * Netty HttpRequest
	 */
	private HttpRequest request;

	/**
	 * HTTP Headers
	 */
	private Properties headers = new Properties();;

	/**
	 * HTTP Paraments
	 */
	// Map<String, List<String>> params;
	private Properties params = new Properties();;

	/**
	 * 
	 */
	public NettyContext(HttpRequest request) {
		this.request = request;

		// set headers
		HttpHeaders hh = request.headers();
		if (!hh.isEmpty()) {
			for (Map.Entry<String, String> h : hh) {
				String key = h.getKey();
				String value = h.getValue();
				headers.setProperty(key, value);
			}
		}

		// set params
		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
		Map<String, List<String>> pp = queryStringDecoder.parameters();
		if (!pp.isEmpty()) {
			for (Entry<String, List<String>> p : pp.entrySet()) {
				String key = p.getKey();
				List<String> vals = p.getValue();
				for (String val : vals) {
					this.params.setProperty(key, val);
				}
//				this.params.setProperty(key, p.getValue().toString());
			}
		}
	}

	@Override
	public String getHttpMethod() {
		return request.method().toString();
	}

	@Override
	public String getUri() {
		return request.uri();
	}

	@Override
	public String getHeader(String key) {
		return headers.getProperty(key);
	}

	@Override
	public String getHeaders() {
		return headers.toString();
	}

	@Override
	public String getParameter(String key) {
		return params.getProperty(key);

	}

	@Override
	public Properties getParameters() {
		return params;
	}

}
