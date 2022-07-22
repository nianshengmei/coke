package pers.warren.ioc.annotation;

import java.lang.annotation.*;

/**
 * 依赖注入注解
 */

@Target({ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {

    String value() default "";
}
