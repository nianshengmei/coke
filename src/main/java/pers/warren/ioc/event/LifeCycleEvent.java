package pers.warren.ioc.event;

import java.lang.annotation.*;

/**
 * 生命周期事件
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LifeCycleEvent {

    /**
     * 注册beanDefinition的生命周期时间
     */
    Class<? extends Event>[] register() default {};

    Class<? extends Event>[] beforeProcessor() default {};

    Class<? extends Event>[] afterProcessor() default {};


    Class<? extends Event>[] whenFieldInject() default {};

    Class<? extends Event>[] afterInitialization() default {};
}
