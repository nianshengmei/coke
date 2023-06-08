package pers.warren.ioc.core;

import java.lang.reflect.Constructor;

/**
 * 默认的Bean工厂
 *
 * @author warren
 * @since 1.0.0
 */
public class DefaultBeanFactory implements BeanFactory {

    /**
     * 获取bean
     *
     * @param beanName bean名称
     * @return {beanName}以$开头则返回FactoryBean,否则返回普通对象
     * @since 1.0.0
     */
    @Override
    public <R> R getBean(String beanName) {
        Container container = Container.getContainer();
        return container.getBean(beanName);
    }

    /**
     * 获取bean
     *
     * @param beanClz bean类型
     * @return bean实例
     * @since 1.0.0
     */
    @Override
    public <R> R getBean(Class<R> beanClz) {
        Container container = Container.getContainer();
        return container.getBean(beanClz);
    }

    /**
     * 容器中是否有bean
     *
     * @param beanName bean名称
     * @return 有则 true
     * @since 1.0.0
     */
    @Override
    public boolean containsBean(String beanName) {
        Container container = Container.getContainer();
        return container.getBean(beanName) != null;
    }

    /**
     * 容器中是否有bean
     *
     * @param beanClz bean类型
     * @return 有则 true
     * @since 1.0.0
     */
    @Override
    public boolean containsBean(Class<?> beanClz) {
        Container container = Container.getContainer();
        return container.getBean(beanClz) != null;
    }

    /**
     * 是否单例
     *
     * @param beanName bean名称
     * @since 1.0.0
     */
    @Override
    public boolean isSingleton(String beanName) {
        Container container = Container.getContainer();
        return container.getBeanDefinition(beanName).isSingleton();
    }

    /**
     * 创建bean对象
     *
     * @param beanDefinition bean定义
     * @return {@link FactoryBean}
     * @since 1.0.2
     */
    public FactoryBean createBean(BeanDefinition beanDefinition) {
        if (null != beanDefinition.getFactoryBeanClass()) {
            try {
                Class<?> factoryBeanClass = beanDefinition.getFactoryBeanClass();
                Constructor<?> constructor = factoryBeanClass.getConstructor(BeanDefinition.class, BeanFactory.class);
                return (FactoryBean) constructor.newInstance(beanDefinition, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new DefaultFactoryBean(beanDefinition, this);
    }
}
