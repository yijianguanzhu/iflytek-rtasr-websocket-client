package com.yijianguanzhu.iflytek.rtasr.client;

import com.yijianguanzhu.iflytek.rtasr.config.AsrWebSocketClientConfig;
import com.yijianguanzhu.iflytek.rtasr.config.NettyConfiguration;
import com.yijianguanzhu.iflytek.rtasr.model.DefaultNettyConfiguration;
import lombok.NonNull;

/**
 * @author yijianguanzhu 2021年01月07日
 * @since 1.8
 */
public class AsrWebSocketClientFactory {

	public static AsrWebSocketClient buildClient( @NonNull AsrWebSocketClientConfig asrConfig ) {
		return new AsrWebSocketClientImpl( asrConfig, new DefaultNettyConfiguration() );
	}

	public static AsrWebSocketClient buildClient( @NonNull AsrWebSocketClientConfig asrConfig,
			@NonNull NettyConfiguration nettyConfiguration ) {
		return new AsrWebSocketClientImpl( asrConfig, nettyConfiguration );
	}
}
