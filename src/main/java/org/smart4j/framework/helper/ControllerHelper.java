package org.smart4j.framework.helper;

import org.smart4j.framework.annotation.Action;
import org.smart4j.framework.bean.Handler;
import org.smart4j.framework.bean.Request;
import org.smart4j.framework.util.ArrayUtil;
import org.smart4j.framework.util.CollectionUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by sally on 2017/2/12.
 * 用于存放请求与处理器的映射关系
 */
public final class ControllerHelper {
    private static final Map<Request, Handler> ACTION_MAP = new HashMap<Request, Handler>();

    static {
        //获取所有的ControllerClass
        Set<Class<?>> controllerClassSet = ClassHelper.getControllerClassSet();
        if(CollectionUtil.isNotEmpty(controllerClassSet)) {
            //遍历ControllerClass
            for(Class<?> controllerClass : controllerClassSet) {
                //获取该controller的所有方法
                Method[] methods = controllerClass.getDeclaredMethods();
                if(ArrayUtil.isNotEmpty(methods)) {
                    //遍历congroller的所有方法
                    for (Method method : methods) {
                        //判断方法中是否有action注解标记
                        if(method.isAnnotationPresent(Action.class)) {
                            //获取action标记的一行注解
                            Action action = method.getAnnotation(Action.class);
                            //获取action标记的内容
                            String mapping = action.value();
                            //判断url映射规则
                            if(mapping.matches("\\w+:/\\w*")) {
                                //将url分割成数组
                                String[] array = mapping.split(":");
                                if(ArrayUtil.isNotEmpty(array) && array.length == 2) {
                                    //获取请求方法 get,post,delete,put
                                    String requestMethod = array[0];
                                    //获取请求路径
                                    String requestPath = array[1];
                                    Request request = new Request(requestMethod,requestPath);
                                    Handler handler = new Handler(controllerClass, method);
                                    //放入Map<Request, Handler>中
                                    ACTION_MAP.put(request,handler);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    /*
    获取handler
     */
    public static Handler getHandler(String requestMethod, String requestPath) {
        //走的是request的构造方法
        Request request = new Request(requestMethod,requestPath);
        //map的key,value，根据key取value
        return  ACTION_MAP.get(request);
    }
}
