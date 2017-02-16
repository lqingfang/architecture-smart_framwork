package org.smart4j.framework.helper;

import org.smart4j.framework.annotation.Aspect;
import org.smart4j.framework.proxy.AspectProxy;
import org.smart4j.framework.proxy.Proxy;
import org.smart4j.framework.proxy.ProxyManager;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Created by sally on 2017/2/15.
 */
public final class AopHelper {
    static {
        try {
            //获取 Map<代理类,目标类集合> 的映射关系
            Map<Class<?>, Set<Class<?>>> proxyMap = createProxyMap();
            //获取  Map<目标类，代理类实体列表>
            Map<Class<?>, List<Proxy>> targetMap = createTargetMap(proxyMap);
            for (Map.Entry<Class<?>, List<Proxy>> targetEntity : targetMap.entrySet()) {
                //获取目标类
                Class<?> targetClass = targetEntity.getKey();
                //获取代理类实体列表
                List<Proxy> proxyList = targetEntity.getValue();
                //创建代理类对象
                Object proxy = ProxyManager.createProxy(targetClass, proxyList);
                //将代理类对象放入Bean_Map中
                BeanHelper.setBean(targetClass, proxy);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
        return Map<Class<?>, Set<Class<?>>>  获取Map<代理类,目标类集合> 的映射关系
     */
    private static Map<Class<?>, Set<Class<?>>> createProxyMap() throws Exception {
        Map<Class<?>, Set<Class<?>>> proxyMap = new HashMap<Class<?>, Set<Class<?>>>();
        //用来获取（Aspect）指定类的   子类/实现类
        Set<Class<?>> proxyClassSet = ClassHelper.getClassSetBySuper(AspectProxy.class);
        //遍历获取到的类
        for (Class<?> proxyClass : proxyClassSet) {
            //判断是否包含(Aspect)指定类型的注解
            if (proxyClass.isAnnotationPresent(Aspect.class)) {
                //返回该程序元素存在的指定类型的注解
                Aspect aspect = proxyClass.getAnnotation(Aspect.class);
                //获取aspect注解的所有类集合
                Set<Class<?>> targetClassSet = createTargetClassSet(aspect);
                proxyMap.put(proxyClass, targetClassSet);
            }
        }
        return proxyMap;
    }
    /*
    获取aspect注解的所有类
     */
    private static Set<Class<?>> createTargetClassSet(Aspect aspect) throws Exception {
        Set<Class<?>> targetClassSet = new HashSet<Class<?>>();
        Class<? extends Annotation> annotation = aspect.value();
        if (annotation != null && !annotation.equals(Aspect.class)) {
            targetClassSet.addAll(ClassHelper.getClassSetByAnnotation(annotation));
        }
        return targetClassSet;
    }
    /*
    分析出 Map<代理类,目标类集合>   之间的映射关系
    return Map<目标类，代理类实体列表>  （1：n）
     */
    private static Map<Class<?>, List<Proxy>> createTargetMap(Map<Class<?>,Set<Class<?>>> proxyMap) throws Exception {
        Map<Class<?>, List<Proxy>> targetMap = new HashMap<Class<?>, List<Proxy>>();
        //遍历  Map<代理类,目标类集合>
        for (Map.Entry<Class<?>, Set<Class<?>>> proxyEntity : proxyMap.entrySet()) {
            //获取  代理类
            Class<?> proxyClass = proxyEntity.getKey();
            //获取   目标类集合
            Set<Class<?>> targetClassSet = proxyEntity.getValue();
            //遍历目标类集合
            for (Class<?> targetClass : targetClassSet) {
                //获取目标类的 代理类实体
                Proxy proxy = (Proxy) proxyClass.newInstance();
                //判断 targetMap 是否有 目标类的键
                if (targetMap.containsKey(targetClass)) {
                    //有的话，直接将 获取的代理类实体  添加到对应的list中
                    targetMap.get(targetClass).add(proxy);
                } else {
                    List<Proxy> proxyList = new ArrayList<Proxy>();
                    proxyList.add(proxy);
                    targetMap.put(targetClass, proxyList);
                }
            }
        }
        return targetMap;
    }

}
