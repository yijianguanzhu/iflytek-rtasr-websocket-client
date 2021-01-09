package com.yijianguanzhu.iflytek.rtasr.config;

/**
 * 默认使用 {@link com.yedan8.xfyun.websocket.model.DefaultNettyConfiguration}
 */

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.concurrent.ExecutorService;

/**
 * @author yijianguanzhu 2021年01月07日
 * @since 1.8
 */
public interface NettyConfiguration {

	/**
	 * 设置消息处理线程池
	 * 
	 * @return
	 */
	ExecutorService executor();

	/**
	 * 设置netty线程组
	 * 
	 * @return
	 */
	NioEventLoopGroup group();

	/**
	 * 设置netty 客户端启动辅助类
	 * 
	 * @return
	 */
	Bootstrap bootstrap();

}
