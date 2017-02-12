package org.smart4j.framework;

import org.smart4j.framework.helper.BeanHelper;
import org.smart4j.framework.helper.ClassHelper;
import org.smart4j.framework.helper.ConfigHelper;
import org.smart4j.framework.helper.IocHelper;
import org.smart4j.framework.util.ClassUtil;

/**
 * Created by sally on 2017/2/12.
 */
public final class HelperLoader {
    public static void init() {
        Class<?>[] classList = {
                ClassHelper.class,
                BeanHelper.class,
                ConfigHelper.class,
                IocHelper.class
        };
        for (Class<?> cls : classList) {
            ClassUtil.loadClass(cls.getName(),false);
        }
    }
}
