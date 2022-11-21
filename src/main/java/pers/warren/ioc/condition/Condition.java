package pers.warren.ioc.condition;

import pers.warren.ioc.core.AnnotationMetadata;

public interface Condition {

    boolean matches(ConditionContext context, AnnotationMetadata metadata);
}
