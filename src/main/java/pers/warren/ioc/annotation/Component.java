package pers.warren.ioc.annotation;


import java.lang.annotation.*;

/**
 * 声明组件的注解
 *
 * @author warren
 * @since jdk1.8
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {

    String name() default "";

    String value() default "";
}
