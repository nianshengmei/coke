package pers.warren.ioc.core;

import java.lang.reflect.InvocationTargetException;

public interface FactoryBean<T> {

    <T> T getObject() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException;

    Class<T> getType();

    Boolean isSingleton();
}
