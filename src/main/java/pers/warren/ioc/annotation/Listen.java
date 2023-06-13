package pers.warren.ioc.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Listen {

    String[] value();

    /**
     * 用哪个线程池处理
     */
    String poolName() default "";
}
