package pers.warren.ioc.core;

/**
 * FactoryBean定义接口
 *
 * @author warren
 * @since jdk1.8
 */
public interface FactoryBean<T> {

    <T> T getObject();

    Class<T> getType();

    Boolean isSingleton();
}
