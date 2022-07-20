package pers.warren.ioc.core;

public class DefaultBeanFactory implements BeanFactory {

    private Container container;

    public DefaultBeanFactory() {
        this.container = Container.getContainer();
    }

    @Override
    public <R> R getBean(String beanName) {
        return container.getBean(beanName);
    }

    @Override
    public <R> R getBean(Class<R> beanClz) {
        return container.getBean(beanClz);
    }

    @Override
    public boolean containsBean(String beanName) {
        return container.getBean(beanName) != null;
    }

    @Override
    public boolean containsBean(Class<?> beanClz) {
        return container.getBean(beanClz) != null;
    }

    @Override
    public boolean isSingleton(String beanName) {
        return container.getBeanDefinition(beanName).isSingleton();
    }

    public FactoryBean createBean(BeanDefinition beanDefinition){
        beanDefinition.
    }
}
