package pers.warren.ioc.condition;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConditionalProperty {

    String key() default "";

    String value() default "";
}
