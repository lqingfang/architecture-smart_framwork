package org.smart4j.framework.annotation;

import java.lang.annotation.*;

/**
 * 切面注解
 * Created by sally on 2017/2/15.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Aspect {
    Class<? extends Annotation> value();
}
