package pers.warren.ioc.core;

import pers.warren.ioc.annotation.Autowired;

import javax.annotation.Resource;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DefaultFactoryBean implements FactoryBean {

    private Class<?> clz;

    public DefaultFactoryBean(Class<?> clz) {
        this.clz = clz;
    }

    @Override
    public Object getObject() {
        Constructor constructor = null;
        try {
            constructor = clz.getConstructor();
        } catch (Exception e) {
        }
        Constructor<?>[] constructors = clz.getConstructors();
        for (Constructor<?> c : constructors) {
            if (null != c.getAnnotation(Autowired.class)) {
                constructor = c;
                break;
            }
            if (null != c.getAnnotation(Resource.class)) {
                constructor = c;
                break;
            }
        }
        try {
            return constructor.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Class getType() {
        return null;
    }

    @Override
    public Boolean isSingleton() {
        return null;
    }
}
