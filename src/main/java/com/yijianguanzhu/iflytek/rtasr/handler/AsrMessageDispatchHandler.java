package com.yijianguanzhu.iflytek.rtasr.handler;

/**
 * 异步消息分发处理器
 */

import com.yijianguanzhu.iflytek.rtasr.enums.Action;
import com.yijianguanzhu.iflytek.rtasr.exception.AsrException;
import com.yijianguanzhu.iflytek.rtasr.model.AsrResponse;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * @author yijianguanzhu 2021年01月08日
 * @since 1.8
 */
@Slf4j
@Setter
public class AsrMessageDispatchHandler extends ChannelDuplexHandler {

	private Consumer<? super AsrException> onError;
	private Consumer<AsrResponse> onStarted;
	private Consumer<AsrResponse> onMessage;
	private ExecutorService executorService;
	private CountDownLatch latch;

	@Override
	public void channelRead( ChannelHandlerContext ctx, Object msg ) throws Exception {
		if ( msg instanceof AsrResponse ) {
			AsrResponse asrResponse = ( AsrResponse ) msg;
			if ( asrResponse.getAction() == Action.STARTED ) {
				this.latch.countDown();
				if ( onStarted != null ) {
					executorService.execute( () -> onStarted.accept( asrResponse ) );
				}
			}
			else if ( asrResponse.getAction() == Action.RESULT ) {
				executorService.execute( () -> onMessage.accept( asrResponse ) );
			}
			else {
				// 此处不需要手动关闭连接也不需要发送结束标识，科大讯飞会在返回错误后，随带关闭连接，防止资源浪费
				// ctx.close();
				if ( onError != null )
					executorService
							.execute( () -> onError.accept( new AsrException( asrResponse.getCode(), asrResponse.getSid() ) ) );
			}
		}
	}

	// 握手通知
	@Override
	public void userEventTriggered( ChannelHandlerContext ctx, Object evt ) throws Exception {

		// 处理握手事件
		if ( evt instanceof WebSocketClientProtocolHandler.ClientHandshakeStateEvent ) {
			WebSocketClientProtocolHandler.ClientHandshakeStateEvent status = ( WebSocketClientProtocolHandler.ClientHandshakeStateEvent ) evt;
			// 握手超时事件
			if ( status == WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_TIMEOUT ) {
				log.error( "Http 升级 WebSocket协议握手超时，关闭连接。" );
				ctx.close().addListener( future -> {
					if ( future.isSuccess() ) {
						log.info( "连接已成功关闭。" );
					}
					else {
						log.warn( "连接未关闭成功，原因\t", future.cause() );
					}
				} );
			}

			// 服务未响应事件
			if ( status == WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_ISSUED ) {
				log.debug( "Http 升级 WebSocket协议客户端已发出，但服务端尚未响应。" );
			}

			// 握手成功事件
			if ( status == WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE ) {
				log.debug( "Http 升级 WebSocket协议成功。" );
			}
		}

		// 事件透传
		ctx.fireUserEventTriggered( evt );
	}

	/**
	 * <p>
	 * 当Channel和远程服务断开连接后，仍然发送数据，会抛异常，该方法被调用。
	 */
	@Override
	public void exceptionCaught( ChannelHandlerContext ctx, Throwable cause ) throws Exception {
		onError.accept( new AsrException( cause ) );
		ctx.fireExceptionCaught( cause );
		if ( ctx.channel().isOpen() ) {
			ctx.close();
		}
	}
}
