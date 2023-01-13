package pers.warren.ioc.annotation;

import java.lang.annotation.*;

/**
 * 依赖注入注解
 *
 * 该注解标注于bean的类的字段上，或者setter方法上
 *
 * <p>支持@Bean这样的简单bean的内部注入, </p>
 * <p>支持 @Component,@Service这样的组件bean,</p>
 * <p>支持@onfiguration这样的配置bean</p>
 *
 * @author warren
 */
@Target({ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {

    String value() default "";
}
