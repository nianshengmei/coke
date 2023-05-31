package pers.warren.ioc.inject;


import pers.warren.ioc.core.BeanDefinition;
import pers.warren.ioc.core.BeanPostProcessor;
import pers.warren.ioc.core.BeanRegister;

/**
 * 值注入后置处理器，用来保证每个bean创建出来就能注入值
 */
public class  ValueInjectBeanPostProcessor implements BeanPostProcessor {

    @Override
    public void postProcessAfterInitialization(BeanDefinition beanDefinition, BeanRegister register) {
        ValueInject inject = new ValueInject();
        inject.inject(beanDefinition);
    }
}
