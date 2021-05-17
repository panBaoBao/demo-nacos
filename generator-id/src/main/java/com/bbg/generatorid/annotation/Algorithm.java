package com.bbg.generatorid.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Algorithm {
    /**
     * 算法类型
     * @return
     */
    String type();
}
