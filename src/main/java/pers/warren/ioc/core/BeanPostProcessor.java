package pers.warren.ioc.core;

/**
 * Bean创建拦截器
 *
 * @author warren
 * @since 1.0.0
 */
public interface BeanPostProcessor {

    /**
     * 前置拦截器
     *
     * @since 1.0.0
     */
    default void postProcessBeforeInitialization(BeanDefinition beanDefinition, BeanRegister register) {

    }

    /**
     * 该拦截器会在所有的前置拦截处理完毕后调用
     *
     * @since 1.0.0
     */
    default void postProcessAfterBeforeProcessor(BeanDefinition beanDefinition, BeanRegister register) {

    }

    /**
     * 后置拦截方法
     *
     * @since 1.0.0
     */
    default void postProcessAfterInitialization(BeanDefinition beanDefinition, BeanRegister register) {

    }

}
