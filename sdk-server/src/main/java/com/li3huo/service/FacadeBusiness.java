/**
 * 
 */
package com.li3huo.service;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.li3huo.sdk.App;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.util.CharsetUtil;

/**
 * @author liyan
 *
 */
public class FacadeBusiness {
	static final Logger logger = LogManager.getLogger(FacadeBusiness.class.getName());
	static final Logger perfLog = LogManager.getLogger("PerfLog");
	/** Server上所有的处理器 */
	static Map<String, FacadeProcessor> processors = new HashMap<String, FacadeProcessor>();

	/**
	 * 解锁 Netty I/O Handler
	 * 
	 * @return
	 */
	public static Runnable createNettyJob(final NettyContext ctx, final boolean keepAlive,
			final boolean decoderResult) {
		return new Runnable() {
			@Override
			public void run() {
				/** StopWatch: socket读入完成(r) */
				ctx.logTime("r");
				/** 插入业务逻辑处理 */
				String busi_resp = process(ctx);
				/** StopWatch: 业务处理完成(p) */
				ctx.logTime("p");
				// Build the response object.
				FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, decoderResult ? OK : BAD_REQUEST,
						Unpooled.copiedBuffer(busi_resp, CharsetUtil.UTF_8));
				logger.debug("response.headers():" + response.headers());
				response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

				if (keepAlive) {
					// Add 'Content-Length' header only for a keep-alive
					// connection.
					response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
					// Add keep alive header as per:
					// -
					// http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
					response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
				}

				// Write the response.
				ctx.write(response);
				/** StopWatch: socket写出完成(w) */
				ctx.logTime("w");
				perfLog.info("[" + ctx.getUri() + "] [" + ctx.status() + "] " + ctx.getTimeLog());
				// If keep-alive is off, close the connection once the
				if (!keepAlive) {
					ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
				}
			}
		};
	}

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

		/** 路由逻辑: uri = /prefix/method/others... */
		String uri = ctx.getUri();
		logger.debug("uri:" + uri);
		String prefix = StringUtils.substringBetween(uri, "/", "/");
		String factory_class = App.getProperty("agent.uri." + prefix, null);
		if (factory_class != null) {

			try {

				FacadeProcessor processor = processors.get(prefix);
				if (processor == null) {
					@SuppressWarnings("unchecked")
					Class<FacadeProcessor> classType = (Class<FacadeProcessor>) Class.forName(factory_class);
					processor = ConstructorUtils.invokeExactConstructor(classType);
				}
				return processor.process(prefix, ctx);
			} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException
					| InstantiationException e) {
				logger.fatal("Failed to process on uri[" + uri + "] " + e.getMessage());
			} finally {
				String method = StringUtils.substringBetween(uri, "/" + prefix + "/", "/");
				/** 服务开启debug模式的时候把所有请求打出来 */
				if (App.getProperty("agent.debug", "false").equalsIgnoreCase("true")) {
					logger.debug("=== Distribution Center Method = [" + method + "] [" + ctx.getRemoteAddr()
							+ "] URI = [" + uri + "] PARAMS = [" + ctx.getParameters() + "] REQ = ["
							+ StringUtils.toEncodedString(ctx.getInputStreamArray(), Charset.forName("UTF-8")) + "]");
				}
			}
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
