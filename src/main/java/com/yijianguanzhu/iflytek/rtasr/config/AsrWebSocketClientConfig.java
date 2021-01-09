package com.yijianguanzhu.iflytek.rtasr.config;

import com.yijianguanzhu.iflytek.rtasr.enums.Scope;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NonNull;
import lombok.ToString;

/**
 * @author yijianguanzhu 2021年01月07日
 * @since 1.8
 */
@Getter
@Setter
@Builder
@ToString
public class AsrWebSocketClientConfig {

	/**
	 * 科大讯飞连接地址
	 */
	@NonNull
	private String url;

	/**
	 * appId
	 */
	@NonNull
	private String appId;

	/**
	 * appId对应的secret_key
	 */
	@NonNull
	private String apiKey;

	/**
	 * 标点过滤控制，默认返回标点，punc=0会过滤结果中的标点
	 */
	@Builder.Default
	private int punc = -1;

	/**
	 * 垂直领域个性化参数
	 */
	private Scope pd;
}
