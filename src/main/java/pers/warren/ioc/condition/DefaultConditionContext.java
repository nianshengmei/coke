package pers.warren.ioc.condition;

import pers.warren.ioc.core.BeanDefinitionRegistry;
import pers.warren.ioc.core.Environment;

public class DefaultConditionContext implements ConditionContext{
    @Override
    public BeanDefinitionRegistry getRegistry() {
        return null;
    }

    @Override
    public Environment getEnvironment() {
        return null;
    }
}
