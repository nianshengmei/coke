package pers.warren.ioc.condition;

import java.lang.annotation.*;

/**
 * 注意该注解仅对@Bean定义的bean生效
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConditionalOnBean {
    // 需要作为条件的类的Class对象数组
    Class<?>[] value() default {};

    // 需要作为条件的类的Name,Class.getTypeName()
    String[] typeName() default {};

    // spring容器中bean的名字
    String[] name() default {};
}