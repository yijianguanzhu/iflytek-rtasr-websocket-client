package com.yijianguanzhu.iflytek.rtasr.enums;

/**
 * 结果类型标识
 */

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
public enum Type {

	MIDDLE("1", "中间结果"), FINAL("0", "最终结果");

	@JsonCreator
	public static Type from( String code ) {
		for ( Type type : values() ) {
			if ( type.code.equals( code ) ) {
				return type;
			}
		}
		throw new IllegalArgumentException( "No matching constant for [" + code + "]" );
	}

	private String code;
	private String desc;
}
