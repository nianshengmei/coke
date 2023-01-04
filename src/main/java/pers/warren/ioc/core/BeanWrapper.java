package pers.warren.ioc.core;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BeanWrapper {

    private String name;

    private Class<?> clz;

    private BeanDefinition beanDefinition;

}
