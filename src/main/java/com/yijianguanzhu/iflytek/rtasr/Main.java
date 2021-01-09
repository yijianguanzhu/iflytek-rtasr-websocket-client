package com.yijianguanzhu.iflytek.rtasr;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import com.yijianguanzhu.iflytek.rtasr.client.AsrChannel;
import com.yijianguanzhu.iflytek.rtasr.client.AsrWebSocketClient;
import com.yijianguanzhu.iflytek.rtasr.client.AsrWebSocketClientFactory;
import com.yijianguanzhu.iflytek.rtasr.config.AsrWebSocketClientConfig;
import com.yijianguanzhu.iflytek.rtasr.model.AsrRecognizeResult;
import com.yijianguanzhu.iflytek.rtasr.model.AsrRecognizeResult.AsrRecognizeResultCn;
import com.yijianguanzhu.iflytek.rtasr.model.AsrRecognizeResult.AsrRecognizeResultCw;
import com.yijianguanzhu.iflytek.rtasr.model.AsrRecognizeResult.AsrRecognizeResultRt;
import com.yijianguanzhu.iflytek.rtasr.model.AsrRecognizeResult.AsrRecognizeResultSt;
import com.yijianguanzhu.iflytek.rtasr.model.AsrRecognizeResult.AsrRecognizeResultWs;
import com.yijianguanzhu.iflytek.rtasr.model.AsrResponse;
import com.yijianguanzhu.iflytek.rtasr.utils.Message2BeanUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 功能测试类
 */
/**
 * @author yijianguanzhu 2021年01月08日
 * @since 1.8
 */
@Slf4j
public class Main {
	static TargetDataLine targetDataLine = null;
	// 每次音频转写会话，都会新生成一个AsrChannel对象
	static AsrChannel asrChannel = null;
	// 线程安全，AsrWebSocketClient设置成全局唯一变量
	static AsrWebSocketClient asrWebSocketClient = null;
	// 线程安全，AsrWebSocketClientConfig设置成全局唯一变量
	static AsrWebSocketClientConfig config = null;
	// 你的appid
	public static String appId = null;
	// 你的appid对应的appkey
	public static String apiKey = null;

	public static void main( String[] args ) {
		initWebSoskcetClient();
		// 请在提示可以录音15秒内按任意键，否则15秒后，科大讯飞将会关闭未输入音频的通道
		soundRecord();
	}

	private static void initWebSoskcetClient() {
		// 科大讯飞基础配置
		config = AsrWebSocketClientConfig.builder()
				.appId( appId )
				.apiKey( apiKey )
				.url( "wss://rtasr.xfyun.cn/v1/ws" )
				.build();
		// 构建全局客户端
		asrWebSocketClient = AsrWebSocketClientFactory.buildClient( config );
		// 获取会话连接
		asrChannel = asrWebSocketClient.onMessage( asrResponse -> {
			log.info( "识别结果：{}", transformMessage( asrResponse ) );
		} );
		// 获取异常消息
		asrChannel.onError( asrException -> {
			log.error( "异常：", asrException );
		} );
		// 获取和科大讯飞握手成功消息
		asrChannel.onStarted( asrResponse -> {
			log.info( "已和科大讯飞服务端握手成功." );
		} );
		// 等待和科大讯飞握手成功
		asrChannel.awaitOpen();
	}

	private static String transformMessage( AsrResponse asrResponse ) {
		try {
			AsrRecognizeResult result = Message2BeanUtil.bean( asrResponse.getData(), AsrRecognizeResult.class );
			StringBuilder resultBuilder = new StringBuilder();
			AsrRecognizeResultCn cn = result.getCn();
			AsrRecognizeResultSt st = cn.getSt();
			List<AsrRecognizeResultRt> rt = st.getRt();
			rt.forEach( rtRes -> {
				List<AsrRecognizeResultWs> ws = rtRes.getWs();
				ws.forEach( wsRes -> {
					List<AsrRecognizeResultCw> cw = wsRes.getCw();
					cw.forEach( cwRes -> {
						String w = cwRes.getW();
						resultBuilder.append( w );
					} );
				} );
			} );
			return resultBuilder.toString();
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
		return null;
	}

	private static AudioFormat getAudioFormat() {
		// 16k采样率
		float sampleRate = 16000F;
		// 16 每个样本中的位数
		int sampleSizeInBits = 16;
		// 1,2 信道数（单声道为 1，立体声为 2，等等）
		int channels = 1;
		// true,false
		boolean signed = true;
		// true,false 指示是以 big-endian 顺序还是以 little-endian 顺序存储音频数据。
		boolean bigEndian = false;
		return new AudioFormat( sampleRate, sampleSizeInBits, channels, signed,
				bigEndian );// 构造具有线性 PCM 编码和给定参数的 AudioFormat。
	}

	@SuppressWarnings("resource")
	private static void soundRecord() {
		System.out.println( "按任意键开始录音，按任意键结束" );
		Scanner input = new Scanner( System.in );
		String Sinput = input.next();
		ExecutorService pool = Executors.newFixedThreadPool( 1 );
		if ( Sinput != null ) {
			// 调用录音方法
			pool.execute( Main::captureAudio );
		}
		Scanner input2 = new Scanner( System.in );
		String Sinput2 = input2.next();
		if ( Sinput2 != null ) {
			targetDataLine.stop();
			targetDataLine.close();
			System.out.println( "录音结束" );
			// 通知结束标识
			asrChannel.complete();
			// 等待科大讯飞识别完最后一段语音。
			asrChannel.await();
			// 生产中，不需要关闭客户端，这里关闭只是测试结束时为了能够退出虚拟机。
			asrWebSocketClient.shutdown();
			pool.shutdown();
		}
	}

	// 捕获音频
	@SuppressWarnings("unused")
	private static void captureAudio() {
		AudioFormat audioFormat = getAudioFormat();
		DataLine.Info dataLineInfo = new DataLine.Info( TargetDataLine.class, audioFormat );
		try {
			targetDataLine = ( TargetDataLine ) AudioSystem.getLine( dataLineInfo );
			targetDataLine.open( audioFormat );
			targetDataLine.start();
			System.out.println( "录音设备已就绪，可开始说话。" );

			int nByte = 0;
			int bufferSize = 3200;
			byte[] buffer = new byte[bufferSize];
			while ( targetDataLine.isOpen() && ( nByte = targetDataLine.read( buffer, 0, bufferSize ) ) > 0 ) {
				if ( !asrChannel.finished() ) {
					asrChannel.send( buffer );
				}
			}
		}
		catch ( LineUnavailableException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
