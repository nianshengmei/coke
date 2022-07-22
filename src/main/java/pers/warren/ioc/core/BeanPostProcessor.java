package pers.warren.ioc.core;

public interface BeanPostProcessor {

    default void postProcessBeforeInitialization(BeanDefinition beanDefinition ,BeanRegister register) {

    }


    default void postProcessAfterInitialization(BeanDefinition beanDefinition , BeanRegister register) {

    }
}
