/**
 * Based on <a href="https://github.com/netty/netty/tree/4.1/example/src/main/java/io/netty/example/http/snoop">Netty 4.1 snoop sample: HttpSnoopServerHandler.java</a>
 */
package com.li3huo.service;

import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.li3huo.sdk.App;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;

public class NettyHttpHandler extends SimpleChannelInboundHandler<Object> {

	static final Logger logger = LogManager.getLogger(NettyHttpHandler.class.getName());
	static final Logger perfLog = LogManager.getLogger("PerfLog");

	private HttpRequest request;

	/** Store read content */
	private final ByteArrayOutputStream readStream = new ByteArrayOutputStream();

	/** 存储除了inputstream之外的其余request信息 */
	private NettyContext context;

	/** Buffer that stores debug info */
	private final StringBuilder buf = new StringBuilder();

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
		if (msg instanceof HttpRequest) {
			HttpRequest request = this.request = (HttpRequest) msg;

			if (HttpUtil.is100ContinueExpected(request)) {
				send100Continue(ctx);
			}

			/** 第一次从socket中读取数据 */
			buf.setLength(0);
			readStream.reset();

			// set "My-Netty-RemoteIP" in header
			InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
			request.headers().set("My-Netty-RemoteIP", address.getAddress().getHostAddress());

			/**
			 * Set HTTP Context
			 * 
			 * start StopWatch
			 */
			this.context = new NettyContext(ctx, request, readStream);
			logger.debug("this.context:" + this.context);
		}

		if (msg instanceof HttpContent) {
			HttpContent httpContent = (HttpContent) msg;

			ByteBuf content = httpContent.content();

			while (content.isReadable()) {
				readStream.write(content.readByte());
			}

			if (msg instanceof LastHttpContent) {

				/** Support for HTTP trailing headers in chunked requests ... */
				LastHttpContent trailer = (LastHttpContent) msg;
				if (!trailer.trailingHeaders().isEmpty()) {
					for (String name : trailer.trailingHeaders().names()) {
						for (String value : trailer.trailingHeaders().getAll(name)) {
							this.context.addTrailingHeader(name, value);
						}
					}
				}

				boolean keepAlive = HttpUtil.isKeepAlive(request);
				boolean decoderResult = trailer.decoderResult().isSuccess();
				logger.info("[request_uri] " + request.uri() + "; [is read OK?] " + decoderResult);

				/** 异步处理业务逻辑和回写 */
				App.execute(FacadeBusiness.createNettyJob(this.context, keepAlive, decoderResult));
				// new Thread(FacadeBusiness.createNettyJob(this.context,
				// keepAlive, decoderResult)).start();
				logger.info("[request_uri] " + request.uri() + "; [read Over] ");

				// if (!writeResponse(trailer, ctx)) {
				// // If keep-alive is off, close the connection once the
				// // content is fully written.
				// ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
				// }
			}
		}
	}

	// private boolean writeResponse(HttpObject currentObj,
	// ChannelHandlerContext ctx) {
	// // Decide whether to close the connection or not.
	// boolean keepAlive = HttpUtil.isKeepAlive(request);
	//
	// logger.info("[request_uri] " + request.uri() + "; [is read OK?] " +
	// currentObj.decoderResult().isSuccess());
	//
	// // /** Set HTTP Context */
	// // if (null == this.context) {
	// // this.context = new NettyContext(request, readStream);
	// // logger.info("new NettyContext()");
	// // }
	//
	// /** StopWatch: socket读入完成(r) */
	// context.logTime("r");
	// /** 插入业务逻辑处理 */
	// String busi_resp = FacadeBusiness.process(this.context);
	// /** StopWatch: 业务处理完成(p) */
	// context.logTime("p");
	// // Build the response object.
	// FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
	// currentObj.decoderResult().isSuccess() ? OK : BAD_REQUEST,
	// Unpooled.copiedBuffer(busi_resp, CharsetUtil.UTF_8));
	// logger.debug("response.headers():" + response.headers());
	// response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;
	// charset=UTF-8");
	//
	// if (keepAlive) {
	// // Add 'Content-Length' header only for a keep-alive connection.
	// response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH,
	// response.content().readableBytes());
	// // Add keep alive header as per:
	// // -
	// //
	// http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
	// response.headers().set(HttpHeaderNames.CONNECTION,
	// HttpHeaderValues.KEEP_ALIVE);
	// }
	//
	// // Encode the cookie.
	// String cookieString = request.headers().get(HttpHeaderNames.COOKIE);
	// if (cookieString != null) {
	// Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieString);
	// if (!cookies.isEmpty()) {
	// // Reset the cookies if necessary.
	// for (Cookie cookie : cookies) {
	// response.headers().add(HttpHeaderNames.SET_COOKIE,
	// ServerCookieEncoder.STRICT.encode(cookie));
	// }
	// }
	// } else {
	// // Browser sent no cookie. Add some.
	// response.headers().add(HttpHeaderNames.SET_COOKIE,
	// ServerCookieEncoder.STRICT.encode("key1", "value1"));
	// response.headers().add(HttpHeaderNames.SET_COOKIE,
	// ServerCookieEncoder.STRICT.encode("key2", "value2"));
	// }
	//
	// // Write the response.
	// ctx.write(response);
	// /** StopWatch: socket写出完成(w) */
	// context.logTime("w");
	// perfLog.info("[" + context.getUri() + "] [" + context.status() + "] " +
	// context.getTimeLog());
	//
	// return keepAlive;
	// }

	private static void send100Continue(ChannelHandlerContext ctx) {
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE);
		ctx.write(response);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// cause.printStackTrace();
		/** Connection reset by peer */
		String remote = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
		logger.error(cause.getMessage() + " " + remote);
		ctx.close();
	}
}