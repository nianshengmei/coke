package pers.warren.ioc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scanner {

    /**
     * 扫描范围数组
     */
    String[] value();
}
