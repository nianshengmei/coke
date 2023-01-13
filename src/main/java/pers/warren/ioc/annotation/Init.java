package pers.warren.ioc.annotation;

import java.lang.annotation.*;

/**
 * bean的初始化方法，一般用于弥补bean在构造函数时期，引用的其他bean未初始化导致的异常
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Init {
}
