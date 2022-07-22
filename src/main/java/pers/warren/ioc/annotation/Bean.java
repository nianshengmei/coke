package pers.warren.ioc.annotation;

import java.lang.annotation.*;

/**
 * 通过函数创建Bean的注解
 *
 * @author warren
 * @since jdk 1.8
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bean {

    String name() default "";
}
