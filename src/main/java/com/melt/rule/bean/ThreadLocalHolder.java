/**
 * 
 */
package com.melt.rule.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * 线程变量
 * @author melt
 * @date 2017-11-06
 */
public class ThreadLocalHolder {
	private final static ThreadLocal<Map<String, Object>> _data = new ThreadLocal<Map<String, Object>>();
	
	/**
	 * 储存变量
	 * @author melt
	 * @date 2017-11-06
	 * @param key
	 * @param parameter
	 * @return
	 */
	public static Object set(String key, Object parameter) {
		checkNotNull(key);
		return getPutIfMissing().put(key, parameter);
	}
	/**
	 * 删除变量
	 * @author melt
	 * @date 2017-11-06
	 * @param key
	 * @param valueType
	 * @return
	 */
	public static <T> T remove(String key, Class<T> valueType) {
		checkNotNull(key, valueType);
		Map<String, Object> map = getPutIfMissing();
		T value = valueType.cast(map.get(key));
		map.remove(key);
		return value;
	}
	/**
	 * 获取变量
	 * @author melt
	 * @date 2017-11-06
	 * @param key
	 * @param valueType
	 * @return
	 */
	public static <T> T get(String key, Class<T> valueType) {
		checkNotNull(key, valueType);
		return valueType.cast(getPutIfMissing().get(key));
	}

	private static Map<String, Object> getPutIfMissing() {
		Map<String, Object> map = _data.get();
		if (map == null) {
			map = new HashMap<String, Object>();
			_data.set(map);
		}
		return map;
	}

	public static void checkNotNull(Object... values) {
		for (Object value : values)
			if (value == null)
				throw new IllegalArgumentException("Null not allowed!");
	}
}
