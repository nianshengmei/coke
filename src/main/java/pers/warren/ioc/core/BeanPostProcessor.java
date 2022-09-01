package pers.warren.ioc.core;

/**
 * Bean创建拦截器
 */
public interface BeanPostProcessor {

    /**
     * 前置拦截器
     */
    default void postProcessBeforeInitialization(BeanDefinition beanDefinition ,BeanRegister register) {

    }

    /**
     * 该拦截器会在所有的前置拦截处理完毕后调用
     */
    default void postProcessAfterBeforeProcessor(BeanDefinition beanDefinition , BeanRegister register){

    }

    /**
     * 后置拦截方法
     */
    default void postProcessAfterInitialization(BeanDefinition beanDefinition , BeanRegister register) {

    }
}
