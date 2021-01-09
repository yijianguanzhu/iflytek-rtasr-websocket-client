package com.yijianguanzhu.iflytek.rtasr.enums;

/**
 * 垂直领域个性化参数
 */

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yijianguanzhu 2021年01月07日
 * @since 1.8
 */
@AllArgsConstructor
@Getter
public enum Scope {

	COURT("court", "法院"), EDU("edu", "教育"), FINANCE("finance", "金融"), MEDICAL("medical", "医疗"), TECH("tech", "科技");

	private String pd;

	private String desc;
}
