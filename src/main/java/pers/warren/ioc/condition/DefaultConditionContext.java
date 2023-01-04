package pers.warren.ioc.condition;

import pers.warren.ioc.core.ApplicationContext;
import pers.warren.ioc.core.BeanDefinitionRegistry;
import pers.warren.ioc.core.Container;
import pers.warren.ioc.core.Environment;

public class DefaultConditionContext implements ConditionContext{

    private final ApplicationContext applicationContext;


    public DefaultConditionContext() {
        this.applicationContext = Container.getContainer().applicationContext();
    }

    @Override
    public BeanDefinitionRegistry getRegistry() {
        return applicationContext;
    }

    @Override
    public Environment getEnvironment() {
        return applicationContext;
    }
}
