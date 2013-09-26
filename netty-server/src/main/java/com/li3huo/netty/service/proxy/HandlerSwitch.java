/**
 * Create at Sep 22, 2013
 */
package com.li3huo.netty.service.proxy;

import javax.net.ssl.SSLEngine;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.compression.ZlibDecoder;
import org.jboss.netty.handler.codec.compression.ZlibEncoder;
import org.jboss.netty.handler.codec.compression.ZlibWrapper;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.ssl.SslHandler;

import com.li3huo.netty.service.ssl.SecureChatSslContextFactory;

/**
 * 在当前的pipeline中动态切换SSL/GZIP及其他Handler
 */

/**
 * 
 * @author liyan
 * 
 */
public class HandlerSwitch extends FrameDecoder {

	private final boolean detectSsl;
	private final boolean detectGzip;

	private HttpIOTransferHandler transferHandler;

	public HandlerSwitch(HttpIOTransferHandler transferHandler) {
		this(true, true, transferHandler);
	}

	private HandlerSwitch(boolean detectSsl, boolean detectGzip,
			HttpIOTransferHandler transferHandler) {
		this.detectSsl = detectSsl;
		this.detectGzip = detectGzip;
		this.transferHandler = transferHandler;
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			ChannelBuffer buffer) throws Exception {
		// Will use the first 5 bytes to detect a protocol.
		if (buffer.readableBytes() < 5) {
			return null;
		}

//		System.out.println(">>> "
//				+ buffer.toString(0, buffer.readableBytes(),
//						Charset.defaultCharset()));

		if (isSsl(buffer)) {
			enableSsl(ctx);
		} else {
			final int magic1 = buffer.getUnsignedByte(buffer.readerIndex());
			final int magic2 = buffer.getUnsignedByte(buffer.readerIndex() + 1);
			if (isGzip(magic1, magic2)) {
				enableGzip(ctx);
			} else if (isHttp(magic1, magic2)) {
				switchToHttpOutboundChannelHandler(ctx);
			} else {
				// Unknown protocol; discard everything and close the
				// connection.
				buffer.skipBytes(buffer.readableBytes());
				ctx.getChannel().close();
				return null;
			}
		}

		// Forward the current read buffer as is to the new handlers.
		return buffer.readBytes(buffer.readableBytes());
	}

	private boolean isSsl(ChannelBuffer buffer) {
		if (detectSsl) {
			return SslHandler.isEncrypted(buffer);
		}
		return false;
	}

	private boolean isGzip(int magic1, int magic2) {
		if (detectGzip) {
			return magic1 == 31 && magic2 == 139;
		}
		return false;
	}

	private static boolean isHttp(int magic1, int magic2) {
		return magic1 == 'G' && magic2 == 'E' || // GET
				magic1 == 'P' && magic2 == 'O' || // POST
				magic1 == 'P' && magic2 == 'U' || // PUT
				magic1 == 'H' && magic2 == 'E' || // HEAD
				magic1 == 'O' && magic2 == 'P' || // OPTIONS
				magic1 == 'P' && magic2 == 'A' || // PATCH
				magic1 == 'D' && magic2 == 'E' || // DELETE
				magic1 == 'T' && magic2 == 'R' || // TRACE
				magic1 == 'C' && magic2 == 'O'; // CONNECT
	}

	private void enableSsl(ChannelHandlerContext ctx) {
		ChannelPipeline p = ctx.getPipeline();

		SSLEngine engine = SecureChatSslContextFactory.getServerContext()
				.createSSLEngine();
		engine.setUseClientMode(false);

		p.addLast("ssl", new SslHandler(engine));
		p.addLast("unificationA", new HandlerSwitch(false, detectGzip,
				transferHandler));
		p.remove(this);
	}

	private void enableGzip(ChannelHandlerContext ctx) {
		ChannelPipeline p = ctx.getPipeline();
		p.addLast("gzipdeflater", new ZlibEncoder(ZlibWrapper.GZIP));
		p.addLast("gzipinflater", new ZlibDecoder(ZlibWrapper.GZIP));
		p.addLast("unificationB", new HandlerSwitch(detectSsl, false,
				transferHandler));
		p.remove(this);
	}

	private void switchToHttpOutboundChannelHandler(ChannelHandlerContext ctx) {
		ChannelPipeline p = ctx.getPipeline();
		p.addLast("decoder", new HttpRequestDecoder());
		p.addLast("encoder", new HttpResponseEncoder());
		p.addLast("deflater", new HttpContentCompressor());
		p.addLast("handler", transferHandler.getNewHandler(ctx.getChannel()));
		p.remove(this);
	}

}
