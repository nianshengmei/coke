package pers.warren.ioc.core;

/**
 * Bean创建拦截器
 */
public interface BeanPostProcessor {

    default void postProcessBeforeInitialization(BeanDefinition beanDefinition ,BeanRegister register) {

    }


    default void postProcessAfterInitialization(BeanDefinition beanDefinition , BeanRegister register) {

    }
}
