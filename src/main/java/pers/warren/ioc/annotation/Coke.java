package pers.warren.ioc.annotation;

import java.lang.annotation.*;

/**
 * 作用在启动类上
 *
 * <p>作用1:将启动类初始化成bean</p>
 * <p>作用2:排除指定类的bean</p>
 * <p>作用3:排除指定名称的bean</p>
 *
 * @author warren
 */
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
