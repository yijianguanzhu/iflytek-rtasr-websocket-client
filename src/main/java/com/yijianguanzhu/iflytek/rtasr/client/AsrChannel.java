package com.yijianguanzhu.iflytek.rtasr.client;

import com.yijianguanzhu.iflytek.rtasr.exception.AsrException;
import com.yijianguanzhu.iflytek.rtasr.handler.AsrMessageDispatchHandler;
import com.yijianguanzhu.iflytek.rtasr.model.AsrResponse;
import com.yijianguanzhu.iflytek.rtasr.model.FinishLatch;
import com.yijianguanzhu.iflytek.rtasr.model.FinishLatchImpl;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPipeline;
import lombok.Setter;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author yijianguanzhu 2021年01月08日
 * @since 1.8
 */
@Slf4j
public class AsrChannel implements FinishLatch {
	private ChannelFuture channelFuture;
	private volatile Channel channel;
	private Consumer<AsrResponse> started;
	private Consumer<AsrException> error;
	private Consumer<AsrResponse> message;
	private ExecutorService executorService;
	private volatile ByteBuffer buf;
	// 缓存大小，默认4KB
	@Setter
	private int capacity = 4 * 1024;
	@Delegate(types = FinishLatch.class)
	private FinishLatchImpl finishLatch;
	private CountDownLatch latch = new CountDownLatch( 1 );
	private static final byte[] END_SIGN = "{\"end\": true}".getBytes();

	public AsrChannel( ChannelFuture channelFuture, Consumer<AsrResponse> message, ExecutorService executorService,
			FinishLatchImpl finishLatch ) {
		this.channelFuture = channelFuture;
		this.message = message;
		this.executorService = executorService;
		this.finishLatch = finishLatch;
		init();
	}

	public void send( byte[] data ) {
		// 未初始化完成，把数据缓存起来
		// 数据缓存不保证并发，正常都会以一定的数据量发送数据流，因此生产理论不存在并发可能性。
		if ( channel == null ) {
			if ( buf == null ) {
				buf = ByteBuffer.allocate( capacity );
				buf.put( data );
			}
			else {
				buf.put( data );
			}
		}
		else if ( channel != null && channel.isOpen() ) {
			if ( buf != null ) {
				buf.put( data );
				channel.writeAndFlush( buf.array() );
				buf = null;
			}
			else {
				channel.writeAndFlush( data );
			}
		}
	}

	// 和科大讯飞握手成功的消息，只有一次
	public void onStarted( Consumer<AsrResponse> asrResponse ) {
		this.started = asrResponse;
		// 该情况说明调用者阻塞或睡眠后才添加函数
		if ( channel != null ) {
			AsrMessageDispatchHandler asrMsgHandler = getAsrMsgHandler();
			asrMsgHandler.setOnStarted( this.started );
		}
	}

	// 错误消息
	public void onError( Consumer<AsrException> asrException ) {
		this.error = asrException;
		// 该情况说明调用者阻塞或睡眠后才添加函数
		if ( channel != null ) {
			AsrMessageDispatchHandler asrMsgHandler = getAsrMsgHandler();
			asrMsgHandler.setOnError( this.error );
		}
	}

	// 判断是否已和科大讯飞服务端连接成功
	public boolean isOpen() {
		return this.latch.getCount() == 0;
	}

	/**
	 * 同步等待，直到和科大讯飞服务端连接成功, 或者发生了异常情况
	 */
	public boolean awaitOpen() throws AsrException {
		return this.awaitOpen( Long.MAX_VALUE, TimeUnit.SECONDS );
	}

	/**
	 * 同步等待，直到和科大讯飞服务端连接成功 或者超时发生
	 * 
	 * @param timeout
	 * @param unit
	 * @throws AsrException
	 */
	public boolean awaitOpen( long timeout, TimeUnit unit ) throws AsrException {
		try {
			return this.latch.await( timeout, unit );
		}
		catch ( Exception e ) {
			throw new AsrException( e );
		}
	}

	// 主动结束通话
	public void complete() {
		if ( channel != null && channel.isOpen() ) {
			channel.writeAndFlush( END_SIGN );
		}
	}

	private void init() {
		channelFuture.addListener( connectFuture -> {
			if ( connectFuture.isSuccess() ) {
				log.debug( "已成功连接到科大讯飞服务器" );
				Channel channel = channelFuture.channel();
				AsrMessageDispatchHandler asrMsgHandler = new AsrMessageDispatchHandler();
				asrMsgHandler.setOnMessage( message );
				asrMsgHandler.setOnStarted( started );
				asrMsgHandler.setOnError( error );
				asrMsgHandler.setExecutorService( executorService );
				asrMsgHandler.setLatch( latch );
				channel.pipeline().addLast( asrMsgHandler );
				this.channel = channel;
				channel.closeFuture().addListener( closeFuture -> {
					log.debug( "连接已关闭" );
					finishLatch.countDown();
				} );
				return;
			}
			// 连接失败
			if ( this.error != null ) {
				log.debug( "连接已关闭" );
				finishLatch.countDown();
				this.error.accept( new AsrException( connectFuture.cause() ) );
			}
		} );
	}

	private AsrMessageDispatchHandler getAsrMsgHandler() {
		ChannelPipeline pipeline = channel.pipeline();
		return pipeline.get( AsrMessageDispatchHandler.class );
	}

}
