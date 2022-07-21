package pers.warren.ioc.core;

public interface FactoryBean<T> {

    <T> T getObject();

    Class<T> getType();

    Boolean isSingleton();
}
