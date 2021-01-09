package com.yijianguanzhu.iflytek.rtasr.enums;

/**
 * 科大讯飞 asr 错误码
 */

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author yijianguanzhu 2021年01月07日
 * @since 1.8
 */
@AllArgsConstructor
@Getter
@ToString
public enum AsrCode {

	SUCCESS("0", "success", "成功", "成功"), ERROR_10105("10105", "illegal access", "没有权限",
			"检查apiKey，ip，ts等授权参数是否正确"), ERROR_10106("10106", "invalid parameter",
					"无效参数", "上传必要的参数， 检查参数格式以及编码"), ERROR_10107("10107", "illegal parameter", "非法参数值",
							"检查参数值是否超过范围或不符合要求"), ERROR_10110("10110", "no license", "无授权许可", "检查参数值是否超过范围或不符合要求"), ERROR_10700(
									"10700", "engine error", "引擎错误", "提供接口返回值，向服务提供商反馈"), ERROR_10202("10202", "websocket connect error",
											"websocket连接错误", "检查网络是否正常"), ERROR_10204("10204", "websocket write error", "服务端websocket写错误",
													"检查网络是否正常，向服务提供商反馈"), ERROR_10205("10205", "websocket read error", "服务端websocket读错误",
															"检查网络是否正常，向服务提供商反馈"), ERROR_16003("16003", "basic component error", "基础组件异常",
																	"重试或向服务提供商反馈"), ERROR_10800("10800", "over max connect limit", "超过授权的连接数",
																			"确认连接数是否超过授权的连接数"), ERROR_99999("99999", "Unknown ASR connection exception",
																					"asr未知连接异常", "查看具体报错日志解决");

	public static AsrCode resolve( String code ) {
		for ( AsrCode error : values() ) {
			if ( error.code.equals( code ) ) {
				return error;
			}
		}
		return null;
	}

	@JsonCreator
	public static AsrCode from( String code ) {
		AsrCode error = resolve( code );
		if ( error == null ) {
			throw new IllegalArgumentException( "No matching constant for [" + code + "]" );
		}
		return error;
	}

	/**
	 * 错误码
	 */
	private String code;
	/**
	 * 描述
	 */
	private String msg;
	/**
	 * 说明
	 */
	private String illustrate;
	/**
	 * 处理方式
	 */
	private String handle;

}
