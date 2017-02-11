package org.smart4j.framework.util;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * JSON工具类
 * @author shuang
 * @version 1.0.0
 */
public final class JsonUtil {
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	/**
	 * 将 POJO 转为JSON
	 * @param obj
	 * @return
	 */
	public static <T> String toJson(T obj) {
		String json;
		try {
			json = OBJECT_MAPPER.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException();
		}
		return json;
	}
	/**
	 * 将JSON转为POJO
	 * @param json
	 * @param type
	 * @return
	 */
	public static <T> T fromJson (String json, Class<T> type) {
		T pojo;
		try {
			pojo = OBJECT_MAPPER.readValue(json, type);
		} catch (Exception e) {
			throw new RuntimeException();
		}
		return pojo;
	}
}
