package com.yijianguanzhu.iflytek.rtasr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yijianguanzhu.iflytek.rtasr.enums.Type;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author yijianguanzhu 2021年01月07日
 * @since 1.8
 */
@Getter
@Setter
@ToString
public class AsrRecognizeResult {

	private AsrRecognizeResultCn cn;

	/**
	 * 转写结果序号 从0开始
	 */
	@JsonProperty("seg_id")
	private int segId;

	@Getter
	@Setter
	@ToString
	public static class AsrRecognizeResultCn {

		private AsrRecognizeResultSt st;

	}

	@Getter
	@Setter
	@ToString
	public static class AsrRecognizeResultSt {

		// 句子在整段语音中的开始时间，单位毫秒(ms) 中间结果的bg为准确值
		private String bg;
		// 句子在整段语音中的结束时间，单位毫秒(ms) 中间结果的ed为0
		private String ed;

		private List<AsrRecognizeResultRt> rt;
		// 结果类型标识 0-最终结果；1-中间结果
		private Type type;
	}

	@Getter
	@Setter
	@ToString
	public static class AsrRecognizeResultRt {

		private List<AsrRecognizeResultWs> ws;
	}

	@Getter
	@Setter
	@ToString
	public static class AsrRecognizeResultWs {

		private List<AsrRecognizeResultCw> cw;
		/**
		 * 词在本句中的开始时间，单位是帧，1帧=10ms
		 * 即词在整段语音中的开始时间为(bg+wb*10)ms 中间结果的 wb 为 0
		 */
		private int wb;
		/**
		 * 词在本句中的结束时间，单位是帧，1帧=10ms
		 * 即词在整段语音中的结束时间为(bg+we*10)ms 中间结果的 we 为 0
		 */
		private int we;
	}

	@Getter
	@Setter
	@ToString
	public static class AsrRecognizeResultCw {
		// 词识别结果
		private String w;
		// 词标识 n-普通词；s-顺滑词（语气词）；p-标点
		private String wp;
	}
}
