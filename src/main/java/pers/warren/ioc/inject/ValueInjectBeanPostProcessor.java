package pers.warren.ioc.inject;


import pers.warren.ioc.core.BeanDefinition;
import pers.warren.ioc.core.BeanPostProcessor;
import pers.warren.ioc.core.BeanRegister;
import pers.warren.ioc.core.Container;

public class  ValueInjectBeanPostProcessor implements BeanPostProcessor {

    @Override
    public void postProcessAfterInitialization(BeanDefinition beanDefinition, BeanRegister register) {
        ValueInject inject = new ValueInject();
        inject.inject(beanDefinition);
    }
}
