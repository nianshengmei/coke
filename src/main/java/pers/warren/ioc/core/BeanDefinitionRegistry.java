package pers.warren.ioc.core;

import java.util.Collection;

public interface BeanDefinitionRegistry {

    /**
     * 注册beanDefinition
     */
    void registerBeanDefinition(String name, BeanDefinition beanDefinition) ;

    /**
     * 移除beanDefinition
     */
    void removeBeanDefinition(String name) ;

    /**
     * 获取beanDefinition
     */
    BeanDefinition getBeanDefinition(String name) ;

    boolean containsBeanDefinition(String name);

    boolean containsBeanDefinition(Class<?> clz);

    String[] getBeanDefinitionNames();

    int getBeanDefinitionCount();

    int getBeanCount(Class<?> clz);

    boolean isBeanNameInUse(String name);

    <T> T getBean(Class<T> clz);

    <T> T getBean(String name);

    Collection<BeanWrapper> getBeanWrappers();
}
