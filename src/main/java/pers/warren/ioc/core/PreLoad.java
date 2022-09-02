package pers.warren.ioc.core;

/**
 * 预加载
 *
 * @author warren
 */
public interface PreLoad {

    /**
     * 指示容器需要预加载的基础组建的类型
     */
    Class<?>[] preloadBasicComponentClass();

    /**
     * 指示容器需要预加载的基础组件的注解类型
     */
    Class<?>[] preloadBasicComponentAnnotationClass();

}
