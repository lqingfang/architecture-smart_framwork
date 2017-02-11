package org.smart4j.framework.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射工具类
 * 
 * @author shuang
 * @version 1.0.0
 */
public final class ReflectionUtil {
	/**
	 * 创建类的实例
	 * @param cls Class类的实例
	 * @return Object 类型的对象
	 */
	public static Object newInstance(Class<?> cls) {
		Object instance = null;
		try {
			instance = cls.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return instance;
	} 
	/**
	 * 调用方法
	 * @param obj Object类的对象
	 * @param method Method类的对象
	 * @param args 可变的Object类型的参数
	 * @return
	 */
	public static Object invokeMethod(Object obj, Method method, Object... args) {
		Object result = null;
		method.setAccessible(true); // 如果获取到的method是private，不设置为true，就会报错
		try {
			method.invoke(obj, args);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 设置成员变量的值	
	 * @param obj Object类的对象
	 * @param field Field类的对象
	 * @param value Object类的对象
	 */
	public static void setField(Object obj, Field field, Object value) {
		field.setAccessible(true); // 如果获取到的field是private，不设置为true，就会报错
		try {
			field.set(obj, value);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
