package pers.warren.ioc.core;

import pers.warren.ioc.event.EventBus;
import pers.warren.ioc.event.LifeCycleEventListener;

public class DefaultPreLoad implements PreLoad{
    @Override
    public Class<?>[] preloadBasicComponentClass() {
        return new Class[]{EventBus.class, LifeCycleEventListener.class};
    }

    @Override
    public Class<?>[] preloadBasicComponentAnnotationClass() {
        return new Class[0];
    }

    @Override
    public Class<?>[] findClasses() {
        return new Class[0];
    }
}
