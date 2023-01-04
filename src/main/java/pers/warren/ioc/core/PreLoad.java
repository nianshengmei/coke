package pers.warren.ioc.core;

/**
 * 预加载bean
 * <p>
 * 注意: PreLoad本身不会被容器初始化,其初始化的只是方法的返回
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

    /**
     * 容器会找到这种类型的所有类
     */
    Class<?>[] findClasses();

}
