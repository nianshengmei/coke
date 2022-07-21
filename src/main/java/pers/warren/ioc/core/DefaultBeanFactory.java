package pers.warren.ioc.core;

public class DefaultBeanFactory implements BeanFactory {

    @Override
    public <R> R getBean(String beanName) {
        Container container = Container.getContainer();
        return container.getBean(beanName);
    }

    @Override
    public <R> R getBean(Class<R> beanClz) {
        Container container = Container.getContainer();
        return container.getBean(beanClz);
    }

    @Override
    public boolean containsBean(String beanName) {
        Container container = Container.getContainer();
        return container.getBean(beanName) != null;
    }

    @Override
    public boolean containsBean(Class<?> beanClz) {
        Container container = Container.getContainer();
        return container.getBean(beanClz) != null;
    }

    @Override
    public boolean isSingleton(String beanName) {
        Container container = Container.getContainer();
        return container.getBeanDefinition(beanName).isSingleton();
    }

    public FactoryBean createBean(BeanDefinition beanDefinition) {
        if (beanDefinition.getFactoryBeanClass().equals(SimpleFactoryBean.class)) {
            return new SimpleFactoryBean(beanDefinition, this);
        }
        return new DefaultFactoryBean(beanDefinition, this);
    }
}
