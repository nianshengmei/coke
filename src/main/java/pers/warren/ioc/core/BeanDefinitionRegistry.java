package pers.warren.ioc.core;

public interface BeanDefinitionRegistry {

    void registerBeanDefinition(String name, BeanDefinition beanDefinition) ;

    void removeBeanDefinition(String name) ;

    BeanDefinition getBeanDefinition(String name) ;

    boolean containsBeanDefinition(String name);

    String[] getBeanDefinitionNames();

    int getBeanDefinitionCount();

    boolean isBeanNameInUse(String name);
}
