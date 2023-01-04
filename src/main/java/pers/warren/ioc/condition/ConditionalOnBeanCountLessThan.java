package pers.warren.ioc.condition;

import java.lang.annotation.*;

/**
 * 仅在该类型的bean的数量小于某个值才注入
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConditionalOnBeanCountLessThan {

    int count() default 99;

    Class<?>[] value() default {};

}
