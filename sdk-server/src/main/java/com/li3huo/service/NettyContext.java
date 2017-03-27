/**
 * 
 */
package com.li3huo.service;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

/**
 * @author liyan
 *
 */
public class NettyContext extends TimeLogger implements FacadeContext {

	/**
	 * Netty HttpRequest
	 */
	private HttpRequest request;

	/**
	 * HTTP Input Stream
	 */
	private ByteArrayOutputStream input;

	/**
	 * @param readBuf
	 * @param request
	 */
	public NettyContext(HttpRequest request, ByteArrayOutputStream input) {
		sw.start();
		this.request = request;
		this.input = input;
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
				if (vals != null)
					this.params.put(key, vals.toArray(new String[vals.size()]));
				// List<String> vals = p.getValue();
				// for (String val : vals) {
				// // this.params.setProperty(key, val);
				// this.params.put(key, val);
				// }
				// this.params.setProperty(key, p.getValue().toString());
			}
		}
	}

	/**
	 * HTTP Headers
	 */
	private Properties headers = new Properties();;

	/**
	 * HTTP Paraments
	 */
	Map<String, String[]> params = new HashMap<String, String[]>();
	// private Properties params = new Properties();;

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
		// return params.getProperty(key);
		// return params.get(key);
		return StringUtils.join(params.get(key));

	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return params;
	}

	@Override
	public String getParameters() {
		StringBuffer sb = new StringBuffer();
		for (Entry<String, String[]> p : params.entrySet()) {
			String key = p.getKey();
			sb.append(key).append("=").append(StringUtils.join(p.getValue()));
			sb.append("&");
		}
		return sb.toString();
	}

	@Override
	public byte[] getInputStreamArray() {
		return input.toByteArray();
	}

	@Override
	public String getRemoteAddr() {
		// X-Forwarded-For
		String remoteIP = this.getHeader("X-Forwarded-For");
		if (null == remoteIP) {
			// set in Netty Handler
			remoteIP = this.getHeader("My-Netty-RemoteIP");
		}
		// for servlet request
		// return this.reqest.getRemoteAddr();

		return remoteIP;
	}

}
