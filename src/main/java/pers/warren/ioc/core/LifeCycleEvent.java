package pers.warren.ioc.core;

/**
 * Bean的生命周期事件
 *
 * @author warren
 * @since 1.0.3
 */
public interface LifeCycleEvent {

    /**
     * 当bean被扫描为BeanDefinition时触发
     */
    void register(BeanDefinition beanDefinition);

    /**
     * 前置处理
     */
    void beforeProcessor(BeanDefinition beanDefinition);


    /**
     * 后置处理
     */
    void afterProcessor(BeanDefinition beanDefinition);

    /**
     * 当注入事件发生时
     */
    void whenFieldInject(String fieldName,Object value);


}
