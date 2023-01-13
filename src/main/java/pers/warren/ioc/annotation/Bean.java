package pers.warren.ioc.annotation;

import java.lang.annotation.*;

/**
 * 通过函数创建Bean的注解
 *
 * <p>对函数的要求是必须是bean的类内部的函数</p>
 *
 * @author warren
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bean {

    String name() default "";
}
