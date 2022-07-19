package pers.warren.ioc.core;

import lombok.Data;
import pers.warren.ioc.enums.BeanType;

@Data
public class BeanDefinition {

    /**
     * bean名称
     */
    private String name;

    /**
     * bean的类型
     */
    private Class<?> clz;

    /**
     * 是否单例
     */
    private boolean singleton;

    /**
     * bean类型
     */
    private BeanType beanType;



}
