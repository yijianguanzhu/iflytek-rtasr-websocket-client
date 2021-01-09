package com.yijianguanzhu.iflytek.rtasr.codec;

/**
 * 消息转换
 */

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

/**
 * @author yijianguanzhu 2021年01月07日
 * @since 1.8
 */
@Sharable
public class AsrMessageToMessageCodec extends MessageToMessageCodec<TextWebSocketFrame, byte[]> {

	/**
	 * 将消息转成 {@link BinaryWebSocketFrame}
	 */
	@Override
	protected void encode( ChannelHandlerContext ctx, byte[] msg, List<Object> out ) throws Exception {
		ByteBuf buf = ctx.alloc().buffer().writeBytes( msg );
		BinaryWebSocketFrame frame = new BinaryWebSocketFrame( buf );
		out.add( frame );
	}

	/**
	 * 将 {@link TextWebSocketFrame}转成 {@link ByteBuf}
	 */
	@Override
	protected void decode( ChannelHandlerContext ctx, TextWebSocketFrame frame, List<Object> out ) throws Exception {
		ByteBuf content = frame.content();
		// 持有Bytebuf，因为上层会在此方法结束后，释放content。
		ReferenceCountUtil.retain( content );
		out.add( content );
	}
}
