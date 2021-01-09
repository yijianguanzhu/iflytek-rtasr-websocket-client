package com.yijianguanzhu.iflytek.rtasr.client;

/**
 * 接入科大讯飞实时语音转文字 asr
 */

import com.yijianguanzhu.iflytek.rtasr.model.AsrResponse;

import java.util.function.Consumer;

/**
 * @author yijianguanzhu 2021年01月07日
 * @since 1.8
 */
public interface AsrWebSocketClient {

	/**
     * 调用者接收识别消息
	 * 
	 * @param message
     * @return
     */
	AsrChannel onMessage(Consumer<AsrResponse> message);

	/**
     * 关闭netty线程组及消息分发线程池
	 */
	void shutdown();
}
