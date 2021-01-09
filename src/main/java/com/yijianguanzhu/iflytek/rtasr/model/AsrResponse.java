package com.yijianguanzhu.iflytek.rtasr.model;

/**
 * 识别结果
 */

import com.yijianguanzhu.iflytek.rtasr.enums.Action;
import com.yijianguanzhu.iflytek.rtasr.enums.AsrCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author yijianguanzhu 2021年01月07日
 * @since 1.8
 */
@Getter
@Setter
@ToString
public class AsrResponse {

	// 结果标识，started:握手，result:结果，error:异常
	private Action action;
	// 结果码
	private AsrCode code;
	// 结果数据
	private String data;
	// 描述
	private String desc;
	// 会话ID，主要用于DEBUG追查问题，如果出现问题，可以提供sid给科大讯飞帮助确认问题。
	private String sid;
}
