package org.smart4j.framework.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sally on 2017/2/12.
 * 测试类
 */
public class test {
    public static void main(String[] args) {
        Map<String, Object> hm = new HashMap<String, Object>();
        Field[] fields = hm.getClass().getDeclaredFields();
        for (Field field:fields) {
            System.out.println(field.getType());
            System.out.println(field.toString());
        }
    }
}
