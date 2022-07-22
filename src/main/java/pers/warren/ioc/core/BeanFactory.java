package pers.warren.ioc.core;

/**
 * Bean工厂
 *
 * @author warren
 * @since jdk1.8
 */
public interface BeanFactory {

    /**
     * 获取bean
     * @param beanName bean名称
     * @return {beanName}以$开头则返回FactoryBean,否则返回普通对象
     */
    <R> R getBean(String beanName);

    /**
     * 获取bean
     * @param beanClz bean类型
     * @return bean实例
     */
    <R> R getBean(Class<R> beanClz);

    /**
     * 容器中是否有bean
     * @param beanName bean名称
     * @return 有则 true
     */
    boolean containsBean(String beanName);

    /**
     * 容器中是否有bean
     * @param beanClz bean类型
     * @return 有则 true
     */
    boolean containsBean(Class<?> beanClz);

    /**
     * 是否单例
     * @param beanName bean名称
     */
    boolean isSingleton(String beanName);

    /**
     * 创建bean对象
     * @param beanDefinition bean定义
     * @return {@link FactoryBean}
     */
    FactoryBean createBean(BeanDefinition beanDefinition);
}
