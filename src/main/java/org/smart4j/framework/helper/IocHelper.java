package org.smart4j.framework.helper;

import org.smart4j.framework.annotation.Inject;
import org.smart4j.framework.util.ArrayUtil;
import org.smart4j.framework.util.CollectionUtil;
import org.smart4j.framework.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by sally on 2017/2/12.
 * 实现依赖注入功能
 */
public final class IocHelper {
    static {
        //获取所有的map<类，实例bean>
        Map<Class<?>, Object> beanMap = BeanHelper.getBeanMap();
        if(CollectionUtil.isNotEmpty(beanMap)) {
            //遍历所有的map<类，实例bean>
            for (Map.Entry<Class<?>, Object> beanEntity:beanMap.entrySet()) {
                //获取所有键，类名
                Class<?> beanClass = beanEntity.getKey();
                //获取所有值，实例bean
                Object beanInstance = beanEntity.getValue();
                //获取 bean 类定义的所有成员变量 （简称 bean field）
                Field[] beanFields = beanClass.getDeclaredFields();
                if (ArrayUtil.isNotEmpty(beanFields)) {
                    //遍历 bean field
                    for (Field beanField : beanFields) {
                        //判断当前  bean field 是否带有 inject注解
                        if (beanField.isAnnotationPresent(Inject.class)) {
                            //在beanMap 中 获取  bean field 对应的实例
                            Class<?> beanFieldClass = beanField.getType();
                            Object beanFieldInstance = beanMap.get(beanFieldClass);
                            if (beanFieldInstance != null) {
                                //通过反射初始化beanField的值
                                ReflectionUtil.setField(beanInstance,beanField,beanFieldInstance);
                            }
                        }
                    }
                }
            }
        }
    }
}
