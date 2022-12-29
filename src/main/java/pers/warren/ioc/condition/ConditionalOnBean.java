package pers.warren.ioc.condition;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnBeanCondition.class)
public @interface ConditionalOnBean {
    // 需要作为条件的类的Class对象数组
    Class<?>[] value() default {};

    // 需要作为条件的类的Name,Class.getTypeName()
    String[] typeName() default {};

    // spring容器中bean的名字
    String[] name() default {};
}