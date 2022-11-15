package pers.warren.ioc.condition;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnBeanCondition.class)
public @interface ConditionalOnBean {
    // 需要作为条件的类的Class对象数组
    Class<?>[] value() default {};

    // 需要作为条件的类的Name,Class.getName()
    String[] type() default {};

    // (用指定注解修饰的bean)条件所需的注解类
    Class<? extends Annotation>[] annotation() default {};

    // spring容器中bean的名字
    String[] name() default {};

    // 可能在其泛型参数中包含指定bean类型的其他类
    Class<?>[] parameterizedContainer() default {};
}