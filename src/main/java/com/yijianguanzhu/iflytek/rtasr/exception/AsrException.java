package com.yijianguanzhu.iflytek.rtasr.exception;

import com.yijianguanzhu.iflytek.rtasr.enums.AsrCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author yijianguanzhu 2021年01月07日
 * @since 1.8
 */
@Getter
@ToString
public class AsrException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AsrCode asrError;
	private String sid;

	public AsrException(AsrCode asrError, String sid ) {
		super( asrError.getMsg() );
		this.sid = sid;
		this.asrError = asrError;
	}

	public AsrException(String errMsg ) {
		super( errMsg );
		this.asrError = AsrCode.ERROR_99999;
	}

	public AsrException(Throwable t ) {
		super( AsrCode.ERROR_99999.getMsg(), t );
		this.asrError = AsrCode.ERROR_99999;
	}
}
