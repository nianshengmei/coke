package pers.warren.ioc.annotation;

import java.lang.annotation.*;
/**
 * bean初始化优先于
 *
 * @author warren
 * @since v1.0.2
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Before {

    String[] name() default {};
}
