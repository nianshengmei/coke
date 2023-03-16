package pers.warren.ioc.core;

public class LazyBeanFactory extends DefaultBeanFactory{

    @Override
    public FactoryBean createBean(BeanDefinition beanDefinition) {
        return new LazyFactoryBean(beanDefinition, this);
    }
}
