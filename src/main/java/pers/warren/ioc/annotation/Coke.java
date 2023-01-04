package pers.warren.ioc.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Coke {

    /**
     * 排除指定类的bean
     */
    Class<?>[] excludeClass() default {};

    /**
     * 排除指定名称的bean
     */
    String[] excludeBeanName() default {};
}
