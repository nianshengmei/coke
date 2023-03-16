package pers.warren.ioc.annotation;

import java.lang.annotation.*;

/**
 * 懒加载注解
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lazy {
}
