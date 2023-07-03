package pers.warren.ioc.core;

import pers.warren.ioc.event.EventBus;
import pers.warren.ioc.event.LifeCycleEventListener;

/**
 * 默认的预加载器
 *
 * @author warren
 */
public class DefaultPreLoad implements PreLoad {

    /**
     * 预加载 事件总线、生命周期事件监听器
     *
     * @auther warren
     * @since 1.0.3
     */
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
