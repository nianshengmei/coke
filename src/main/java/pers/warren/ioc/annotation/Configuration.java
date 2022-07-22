package pers.warren.ioc.annotation;

import java.lang.annotation.*;

/**
 * 配置类注解
 *
 * @author warren
 * @since jdk1.8
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Configuration {

    String[] scanner() default {};

    String value() default "";

    String name() default "";
}
