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
     * 后置拦截方法
     */
    default void postProcessAfterInitialization(BeanDefinition beanDefinition , BeanRegister register) {

    }

    /**
     * bean加载后置拦截
     */
    default void postProcessAfterBeanLoad(BeanDefinitionRegistry register){

    }
}
