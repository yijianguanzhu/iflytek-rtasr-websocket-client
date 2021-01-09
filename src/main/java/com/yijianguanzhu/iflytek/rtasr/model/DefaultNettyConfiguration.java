package com.yijianguanzhu.iflytek.rtasr.model;

import com.yijianguanzhu.iflytek.rtasr.config.NettyConfiguration;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yijianguanzhu 2021年01月07日
 * @since 1.8
 */
public class DefaultNettyConfiguration implements NettyConfiguration {

	@Override
	public ExecutorService executor() {
		return new ThreadPoolExecutor(
				Runtime.getRuntime().availableProcessors() * 2, 999, 600L, TimeUnit.SECONDS,
				new LinkedBlockingQueue<>( 99999 ), new ThreadPoolExecutor.CallerRunsPolicy() );
	}

	@Override
	public NioEventLoopGroup group() {
		NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
		eventExecutors.setIoRatio( 80 );
		return eventExecutors;
	}

	@Override
	public Bootstrap bootstrap() {
		return new Bootstrap().group( group() ).channel( NioSocketChannel.class )
				.option( ChannelOption.TCP_NODELAY, true )
				.option( ChannelOption.SO_KEEPALIVE, true )
				.option( ChannelOption.SO_REUSEADDR, false );
	}
}
