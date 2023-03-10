package pers.warren.ioc.core;

import java.util.List;

public class DefaultContainer extends AbstractContainer{
    @Override
    public <T> T getBean(String name) {
        return null;
    }

    @Override
    public <T> T getBean(Class<?> clz) {
        return null;
    }

    @Override
    public <T> List<T> getBeans(Class<?> clz) {
        return null;
    }
}
