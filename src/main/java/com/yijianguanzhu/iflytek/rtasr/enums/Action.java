package com.yijianguanzhu.iflytek.rtasr.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author yijianguanzhu 2021年01月08日
 * @since 1.8
 */
@AllArgsConstructor
@Getter
@ToString
public enum Action {

	STARTED("started", "握手"), RESULT("result", "结果"), ERROR("error", "异常");

	@JsonCreator
	public static Action from( String code ) {
		for ( Action action : values() ) {
			if ( action.code.equals( code ) ) {
				return action;
			}
		}
		throw new IllegalArgumentException( "No matching constant for [" + code + "]" );
	}

	private String code;
	private String desc;
}
