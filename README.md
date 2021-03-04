# iflytek-rtasr-websocket-client

#### 介绍

科大讯飞实时语音转写ASR 基于Netty的WebSocket Client SDK

相关信息查看：https://www.xfyun.cn/doc/asr/rtasr/API.html#%E6%8E%A5%E5%8F%A3%E8%AF%B4%E6%98%8E

#### 使用
	
	@Slf4j
	public class Main {
		public static void main( String[] args ) throws InterruptedException {
			// 线程安全，AsrWebSocketClientConfig设置成全局唯一变量
			AsrWebSocketClientConfig config = AsrWebSocketClientConfig.builder()
					.appId( "你的appid" )
					.apiKey( "你的apikey" )
					.url( "wss://rtasr.xfyun.cn/v1/ws" )
					.build();
			// 线程安全，AsrWebSocketClient设置成全局唯一变量
			AsrWebSocketClient asrWebSocketClient = AsrWebSocketClientFactory.buildClient( config );
			// 每次音频转写会话，都会新生成一个AsrChannel对象
			AsrChannel asrChannel = asrWebSocketClient.onMessage( asrResponse -> {
				log.info( asrResponse.toString() );
			} );
			asrChannel.onError( asrException -> {
				log.error( "异常：", asrException );
			} );
			asrChannel.onStarted( asrResponse -> {
				log.info( "已和科大讯飞服务端握手成功." );
			} );
			// 等待和科大讯飞握手成功
			asrChannel.awaitOpen();
			log.info( "开始" );
			asrChannel.send( "".getBytes() );
			// 通知会话结束标识
			asrChannel.complete();
			// 等待科大讯飞识别完最后一段语音。
			asrChannel.await();
			// 生产中，不需要关闭客户端，这里关闭只是测试结束时为了能够退出虚拟机。
			asrWebSocketClient.shutdown();
		}
	}
	

具体实时音频录入并识别方法可参考源码中的Main函数。

#### 项目
本项目已上传到Maven，项目中引入如下即可使用。

	<dependency>
		<groupId>com.yijianguanzhu.iflytek</groupId>
		<artifactId>iflytek-rtasr-websocket-client</artifactId>
		<version>1.2</version>
	</dependency>

## 边录音边识别用法
在自己工程中引入依赖后，只需要在自己代码中编写如下代码即可，然后根据提示操作。之后你便可以边说话边识别

	import com.yijianguanzhu.iflytek.rtasr.Main;
	/**
	 * @author yijianguanzhu 2021年01月09日
	 * @since 1.8
	 */
	public class AsrTest {
		public static void main( String[] args ) {
			Main.appId = "你的appId";
			Main.apiKey = "你的apiKey";
			Main.main( args );
		}
	}

## 注意事项

1. 本项目直接在idea上运行会报slf4j组件依赖错误问题，无法直接运行，将本项目依赖到自己的spring项目中，试运行就不会出问题了。
2. 本项目直接在eclipse上运行不会报错，因为项目开发时是在eclipse上的。
