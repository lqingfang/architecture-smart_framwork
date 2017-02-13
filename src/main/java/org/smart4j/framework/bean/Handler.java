package org.smart4j.framework.bean;

import java.lang.reflect.Method;

/**
 * 封装action信息
 * 处理器信息
 * 处理 类，方法
 * 
 * @author shuang
 * @version 1.0.0
 */
public class Handler {
	/**
	 * Controller类
	 */
	private Class<?> controllerClass;
	/**
	 * Action方法
	 */
	private Method actionMethod;
	
	public Handler(Class<?> controllerClass, Method actionMethod) {
		this.controllerClass = controllerClass;
		this.actionMethod = actionMethod;
	}

	public Class<?> getControllerClass() {
		return controllerClass;
	}

	public Method getActionMethod() {
		return actionMethod;
	}
}
