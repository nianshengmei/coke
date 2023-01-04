package pers.warren.ioc.core;

import java.util.Collection;

public interface BeanDefinitionRegistry {

    void registerBeanDefinition(String name, BeanDefinition beanDefinition) ;

    void removeBeanDefinition(String name) ;


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
