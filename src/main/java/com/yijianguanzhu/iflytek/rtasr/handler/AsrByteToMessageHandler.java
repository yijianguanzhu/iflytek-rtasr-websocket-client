package com.yijianguanzhu.iflytek.rtasr.handler;

/**
 * 把msg转成 AsrResponse
 */

import com.yijianguanzhu.iflytek.rtasr.model.AsrResponse;
import com.yijianguanzhu.iflytek.rtasr.utils.Message2BeanUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.StandardCharsets;

/**
 * @author yijianguanzhu 2021年01月07日
 * @since 1.8
 */
@Sharable
public class AsrByteToMessageHandler extends ChannelDuplexHandler {

	@Override
	public void channelRead( ChannelHandlerContext ctx, Object msg ) throws Exception {
		if ( msg instanceof ByteBuf ) {
			msg = bean( ( ByteBuf ) msg );
		}
		// 消息透传
		ctx.fireChannelRead( msg );
	}

	private static AsrResponse bean(ByteBuf buf ) throws Exception {
		return Message2BeanUtil.bean( new String( Unpooled.copiedBuffer( buf ).array(), StandardCharsets.UTF_8 ),
				AsrResponse.class );
	}
}
