package org.smart4j.framework.util;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * 编码与解码操作工具类
 * @author shuang
 * @version 1.0.0
 */
public final class CodecUtil {
	/**
	 * 将URL编码
	 * @param source
	 * @return
	 */
	public static String encoderURL(String source) {
		String target;
		try {
			target = URLEncoder.encode(source, "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return target;
	}
	/**
	 * 将URL解码
	 * @param source
	 * @return
	 */
	public static String decodeURL(String source) {
		String target;
		try {
			target = URLDecoder.decode(source, "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return target;
	}
}
