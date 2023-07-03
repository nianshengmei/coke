package pers.warren.ioc.core;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * bean包装类
 *
 * @since 1.0.3
 */
@Data
@Accessors(chain = true)
public class BeanWrapper {

    private String name;

    private Class<?> clz;

    private BeanDefinition beanDefinition;

}
