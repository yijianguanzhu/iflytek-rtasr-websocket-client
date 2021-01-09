package com.yijianguanzhu.iflytek.rtasr.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonParser.Feature;

/**
 * @author yijianguanzhu 2021年01月07日
 * @since 1.8
 */
public class Message2BeanUtil {
	public final static ObjectMapper JACKSONMAPPER = new ObjectMapper();
	static {
		JACKSONMAPPER.setSerializationInclusion( JsonInclude.Include.NON_NULL );
		JACKSONMAPPER.configure( DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false );
		JACKSONMAPPER.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
		JACKSONMAPPER.configure( Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true );
	}

	public static <T> T bean( String jsonString, Class<T> clazz ) throws Exception {
		return JACKSONMAPPER.readValue( jsonString, clazz );
	}
}
