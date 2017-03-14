/**
 * Based on <a href="https://github.com/netty/netty/tree/4.1/example/src/main/java/io/netty/example/http/snoop">Netty 4.1 snoop sample: HttpSnoopServerHandler.java</a>
 */
package com.li3huo.service;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.ByteArrayOutputStream;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.util.CharsetUtil;

public class HttpServerHandler extends SimpleChannelInboundHandler<Object> {

	static final Logger logger = LogManager.getLogger(HttpServerHandler.class.getName());

	private HttpRequest request;

	/** Store read content */
	private final ByteArrayOutputStream readBuf = new ByteArrayOutputStream();

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

			buf.setLength(0);
			/** Set HTTP Context */
			this.context = new NettyContext(request);

			appendDecoderResult(buf, request);
		}

		if (msg instanceof HttpContent) {
			HttpContent httpContent = (HttpContent) msg;

			ByteBuf content = httpContent.content();

			while (content.isReadable()) {
				// try {
				//
				// } catch (IOException e) {
				// logger.error("read socket failed. ", e);
				// }
				readBuf.write(content.readByte());
			}

			if (content.isReadable()) {
				buf.append("CONTENT: ");
				buf.append(content.toString(CharsetUtil.UTF_8));
				buf.append("\r\n");
				appendDecoderResult(buf, request);
			}

			if (msg instanceof LastHttpContent) {
				buf.append("END OF CONTENT\r\n");

				LastHttpContent trailer = (LastHttpContent) msg;
				if (!trailer.trailingHeaders().isEmpty()) {
					buf.append("\r\n");
					for (CharSequence name : trailer.trailingHeaders().names()) {
						for (CharSequence value : trailer.trailingHeaders().getAll(name)) {
							buf.append("TRAILING HEADER: ");
							buf.append(name).append(" = ").append(value).append("\r\n");
						}
					}
					buf.append("\r\n");
				}

				if (!writeResponse(trailer, ctx)) {
					// If keep-alive is off, close the connection once the
					// content is fully written.
					ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
				}
			}
		}
	}

	private static void appendDecoderResult(StringBuilder buf, HttpObject o) {
		DecoderResult result = o.decoderResult();
		if (result.isSuccess()) {
			return;
		}

		buf.append(".. WITH DECODER FAILURE: ");
		buf.append(result.cause());
		buf.append("\r\n");
	}

	private boolean writeResponse(HttpObject currentObj, ChannelHandlerContext ctx) {
		// Decide whether to close the connection or not.
		boolean keepAlive = HttpUtil.isKeepAlive(request);

		logger.info("[request_uri] " + request.uri() + "; [is read OK?] " + currentObj.decoderResult().isSuccess());

		/** Set HTTP Context */
		if (null == this.context) {
			this.context = new NettyContext(request);
		}

		/**
		 * 插入业务逻辑处理
		 */
		String busi_resp = FacadeBusiness.process(this.context, readBuf.toByteArray());

		// Build the response object.
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
				currentObj.decoderResult().isSuccess() ? OK : BAD_REQUEST,
				Unpooled.copiedBuffer(busi_resp, CharsetUtil.UTF_8));

		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

		if (keepAlive) {
			// Add 'Content-Length' header only for a keep-alive connection.
			response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
			// Add keep alive header as per:
			// -
			// http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
			response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
		}

		// Encode the cookie.
		String cookieString = request.headers().get(HttpHeaderNames.COOKIE);
		if (cookieString != null) {
			Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieString);
			if (!cookies.isEmpty()) {
				// Reset the cookies if necessary.
				for (Cookie cookie : cookies) {
					response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
				}
			}
		} else {
			// Browser sent no cookie. Add some.
			response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode("key1", "value1"));
			response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode("key2", "value2"));
		}

		// Write the response.
		ctx.write(response);

		return keepAlive;
	}

	private static void send100Continue(ChannelHandlerContext ctx) {
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE);
		ctx.write(response);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}