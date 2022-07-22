package pers.warren.ioc.annotation;

import java.lang.annotation.*;

/**
 * 配置文件内容注入
 *
 * @author warren
 * @since jdk1.8
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Value {

    String value();
}
