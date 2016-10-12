/**
 * 
 */
package com.li3huo.service;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author liyan
 *
 */
public class TimeEncoder extends MessageToByteEncoder<UnixTime> {
	@Override
	protected void encode(ChannelHandlerContext ctx, UnixTime msg, ByteBuf out) {
		out.writeInt((int) msg.value());
	}
}
