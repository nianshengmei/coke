package pers.warren.ioc.condition;

import pers.warren.ioc.core.BeanDefinitionRegistry;
import pers.warren.ioc.core.Environment;

public interface ConditionContext {

    BeanDefinitionRegistry getRegistry();

    Environment getEnvironment();

}
