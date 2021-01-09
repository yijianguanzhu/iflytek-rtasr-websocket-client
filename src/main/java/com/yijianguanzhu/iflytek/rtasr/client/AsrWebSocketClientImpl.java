package com.yijianguanzhu.iflytek.rtasr.client;

import com.yijianguanzhu.iflytek.rtasr.config.AsrWebSocketClientConfig;
import com.yijianguanzhu.iflytek.rtasr.config.DefaultChannelInitializer;
import com.yijianguanzhu.iflytek.rtasr.config.NettyConfiguration;
import com.yijianguanzhu.iflytek.rtasr.model.AsrResponse;
import com.yijianguanzhu.iflytek.rtasr.model.FinishLatchImpl;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import lombok.NonNull;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * @author yijianguanzhu 2021年01月08日
 * @since 1.8
 */
class AsrWebSocketClientImpl implements AsrWebSocketClient {

	private Bootstrap bootstrap;
	private ExecutorService executorService;
	private URI uri;

	public AsrWebSocketClientImpl( AsrWebSocketClientConfig asrConfig, NettyConfiguration nettyConfiguration ) {
		this.executorService = nettyConfiguration.executor();
		this.uri = URI.create( asrConfig.getUrl() );
		this.bootstrap = nettyConfiguration.bootstrap()
				.handler( new DefaultChannelInitializer( asrConfig ) );
	}

	@Override
	public AsrChannel onMessage( @NonNull Consumer<AsrResponse> message ) {
		final ChannelFuture channelFuture = this.bootstrap
				.connect( this.uri.getHost(), this.uri.getPort() == -1 ? isSsl( this.uri ) ? 443 : 80 : this.uri.getPort() );
		return new AsrChannel( channelFuture, message, this.executorService, new FinishLatchImpl() );
	}

	@Override
	public void shutdown() {
		EventLoopGroup group = this.bootstrap.config().group();
		if ( !group.isShuttingDown() && !group.isShutdown() ) {
			group.shutdownGracefully();
		}
		if ( !executorService.isShutdown() ) {
			executorService.shutdown();
		}
	}

	private boolean isSsl( URI uri ) {
		return uri.getScheme().equals( "wss" );
	}
}
