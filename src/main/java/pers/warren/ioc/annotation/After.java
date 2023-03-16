package pers.warren.ioc.annotation;

import java.lang.annotation.*;
/**
 * bean初始化落后于
 *
 * @author warren
 * @since v1.0.2
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface After {

    String[] name() default {};
}
