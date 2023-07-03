package pers.warren.ioc.core;

/**
 * 懒加载Bean工厂
 *
 * @since 1.0.3
 */
public class LazyBeanFactory extends DefaultBeanFactory{

    @Override
    public FactoryBean createBean(BeanDefinition beanDefinition) {
        return new LazyFactoryBean(beanDefinition, this);
    }
}
