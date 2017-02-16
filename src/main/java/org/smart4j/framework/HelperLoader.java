package org.smart4j.framework;

import org.smart4j.framework.helper.*;
import org.smart4j.framework.util.ClassUtil;

/**
 * Created by sally on 2017/2/12.
 * 初始化框架，该处代码只是为了集中加载helper类
 * （没有此处也可以，当第一次访问类时，就会加载static块）
 */
public final class HelperLoader {
    public static void init() {
        Class<?>[] classList = {
                ClassHelper.class,
                BeanHelper.class,
                AopHelper.class,
                IocHelper.class,
                ConfigHelper.class
        };
        for (Class<?> cls : classList) {
            ClassUtil.loadClass(cls.getName(),false);
        }
    }
}
