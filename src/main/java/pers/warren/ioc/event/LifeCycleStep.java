package pers.warren.ioc.event;

public enum LifeCycleStep {

    REGISTER,

    BEFORE_PROCESSOR,

    AFTER_PROCESSOR,

    AFTER_INITIALIZATION,

    BEAN_INJECT
}
