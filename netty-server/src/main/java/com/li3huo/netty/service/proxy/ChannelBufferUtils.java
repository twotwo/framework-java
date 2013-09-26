/**
 * Create at Sep 23, 2013
 * 
 * copy from https://github.com/shenfeng/async-http-client/blob/master/src/java/me/shenfeng/Utils.java
 * 
 */
package com.li3huo.netty.service.proxy;

import static org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer;
import static org.jboss.netty.handler.codec.compression.ZlibWrapper.GZIP;
import static org.jboss.netty.handler.codec.compression.ZlibWrapper.ZLIB_OR_NONE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_ENCODING;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.TRANSFER_ENCODING;
import static org.jboss.netty.util.CharsetUtil.UTF_8;

import java.nio.charset.Charset;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.compression.ZlibDecoder;
import org.jboss.netty.handler.codec.embedder.CodecEmbedderException;
import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

/**
 * @author liyan
 * 
 */
public class ChannelBufferUtils {

	static Logger logger = Logger.getLogger(ChannelBufferUtils.class);

	private static final String CS = "charset=";

	public static Charset parseCharset(String type) {
		if (type != null) {
			try {
				type = type.toLowerCase();
				int i = type.indexOf(CS);
				if (i != -1) {
					String charset = type.substring(i + CS.length()).trim();
					return Charset.forName(charset);
				}
			} catch (Exception ignore) {
			}
		}
		return null;
	}

	static Charset detectCharset(Charset result, ChannelBuffer content) {
		if (result == null) {
			// decode a little the find charset=???
			byte[] arr = content.array();
			String s = new String(arr, 0, Math.min(350, arr.length), UTF_8);
			int idx = s.indexOf(CS);
			if (idx != -1) {
				int start = idx + CS.length();
				int end = s.indexOf('"', start);
				if (end != -1) {
					try {
						result = Charset.forName(s.substring(start, end));
					} catch (Exception ignore) {
					}
				}
			}
		}
		return result;
	}

	public static void logHttpRequest(HttpRequest msg) {
		// method uri protocol
		logger.info(logHttpMessage(msg).insert(
				0,
				"\n" + msg.getMethod() + " " + msg.getUri() + " "
						+ msg.getProtocolVersion()));
	}

	public static void logHttpResponse(HttpResponse msg) {
		// protocol status
		logger.info(logHttpMessage(msg).insert(0,
				"\n" + msg.getProtocolVersion() + " " + msg.getStatus()));
	}

	private static StringBuilder logHttpMessage(HttpMessage msg) {
		StringBuilder buf = new StringBuilder();
		if (!msg.getHeaderNames().isEmpty()) {
			for (String name : msg.getHeaderNames()) {
				for (String value : msg.getHeaders(name)) {
					buf.append("\n" + name + ": " + value);
				}
			}
		}
		buf.append("\n");

		ChannelBuffer content = msg.getContent();
		if (content.readable()) {
			buf.append(ChannelBufferUtils.getBodyStringFromHttpMessage(msg));
			buf.append("\n");

		}
		return buf;
	}

	public static String getBodyStringFromHttpMessage(HttpMessage msg) {
		// TODO it's a bug, should not happen, "http://logos.md/2008/"
		msg.removeHeader(TRANSFER_ENCODING);

		try {
			String contentEncoding = msg.getHeader(CONTENT_ENCODING);
			DecoderEmbedder<ChannelBuffer> decoder = null;
			if ("gzip".equalsIgnoreCase(contentEncoding)
					|| "x-gzip".equalsIgnoreCase(contentEncoding)) {
				decoder = new DecoderEmbedder<ChannelBuffer>(new ZlibDecoder(
						GZIP));
			} else if ("deflate".equalsIgnoreCase(contentEncoding)
					|| "x-deflate".equalsIgnoreCase(contentEncoding)) {
				decoder = new DecoderEmbedder<ChannelBuffer>(new ZlibDecoder(
						ZLIB_OR_NONE));
			}

			ChannelBuffer body = msg.getContent();

			if (decoder != null) {
				decoder.offer(body);
				ChannelBuffer b = wrappedBuffer(decoder
						.pollAll(new ChannelBuffer[decoder.size()]));
				if (decoder.finish()) {
					ChannelBuffer r = wrappedBuffer(decoder
							.pollAll(new ChannelBuffer[decoder.size()]));
					body = wrappedBuffer(b, r);
				} else {
					body = b;
				}
			}
			Charset ch = detectCharset(
					parseCharset(msg.getHeader(CONTENT_TYPE)), body);
			if (ch == null)
				ch = UTF_8;
			return new String(body.array(), 0, body.readableBytes(), ch);
		} catch (CodecEmbedderException e) {
			logger.trace(e.getMessage(), e); // incorrect CRC32 checksum
			return "";
		}
	}
}
