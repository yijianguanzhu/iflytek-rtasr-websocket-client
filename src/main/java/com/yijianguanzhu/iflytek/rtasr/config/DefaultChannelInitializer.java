package com.yijianguanzhu.iflytek.rtasr.config;

/**
 * 默认的链路处理器
 */

import com.yijianguanzhu.iflytek.rtasr.codec.AsrMessageToMessageCodec;
import com.yijianguanzhu.iflytek.rtasr.exception.AsrException;
import com.yijianguanzhu.iflytek.rtasr.handler.AsrByteToMessageHandler;
import com.yijianguanzhu.iflytek.rtasr.utils.EncryptUtil;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolConfig;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.ssl.SslHandler;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.net.URI;
import java.net.URLEncoder;

/**
 * @author yijianguanzhu 2021年01月07日
 * @since 1.8
 */
@Slf4j
public class DefaultChannelInitializer extends ChannelInitializer<SocketChannel> {

	private static final AsrMessageToMessageCodec CODEC = new AsrMessageToMessageCodec();
	private static final AsrByteToMessageHandler HANDLER = new AsrByteToMessageHandler();

	private AsrWebSocketClientConfig asrConfig;
	private String appId;
	private String apiKey;

	public DefaultChannelInitializer( AsrWebSocketClientConfig asrConfig ) {
		this.asrConfig = asrConfig;
		this.appId = asrConfig.getAppId();
		this.apiKey = asrConfig.getApiKey();
	}

	@Override
	protected void initChannel( SocketChannel ch ) throws Exception {
		URI uri = URI.create( asrConfig.getUrl() + getHandShakeParams( appId, apiKey ) );
		WebSocketClientProtocolConfig config = WebSocketClientProtocolConfig.newBuilder()
				.webSocketUri( uri )
				.absoluteUpgradeUrl( true )
				.handshakeTimeoutMillis( 5000L )
				.build();
		ChannelPipeline pipeline = ch.pipeline();
		if ( isSsl( uri ) ) {
			SSLContext sslContext = SSLContext.getInstance( "TLSv1.2" );
			sslContext.init( null, null, null );
			SSLEngine engine = sslContext.createSSLEngine();
			engine.setUseClientMode( true );
			pipeline.addLast( new SslHandler( engine ) );
		}
		pipeline.addLast( new HttpClientCodec() );
		pipeline.addLast( new HttpObjectAggregator( 65536 ) );
		pipeline.addLast( new WebSocketClientProtocolHandler( config ) );
		pipeline.addLast( CODEC );
		pipeline.addLast( HANDLER );
	}

	// 是否开启SSL
	private boolean isSsl( URI uri ) {
		return uri.getScheme().equals( "wss" );
	}

	// 生成握手参数
	private String getHandShakeParams( String appId, String apiKey ) {
		long ts = System.currentTimeMillis() / 1000;
		String signa = "";
		try {
			signa = EncryptUtil.HmacSHA1Encrypt( EncryptUtil.MD5( appId + ts ), apiKey );
			return "?appid=" + appId + "&ts=" + ts + "&signa=" + URLEncoder.encode( signa, "UTF-8" )
					+ getNonRequireParameters();
		}
		catch ( Exception e ) {
			log.error( "生成握手参数失败", e );
			throw new AsrException( e );
		}
	}

	// 构建非必需参数
	private String getNonRequireParameters() {
		StringBuilder stringBuilder = new StringBuilder();
		if ( asrConfig.getPunc() == 0 ) {
			stringBuilder.append( "&punc=0" );
		}
		if ( asrConfig.getPd() != null ) {
			stringBuilder.append( "&pd=" + asrConfig.getPd().getPd() );
		}
		return stringBuilder.toString();
	}
}
