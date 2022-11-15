package pers.warren.ioc.condition;

import pers.warren.ioc.core.AnnotationMetadata;

public class OnBeanCondition implements Condition{
    @Override
    public boolean matches(ConditionContext context, AnnotationMetadata metadata) {
        return false;
    }
}
