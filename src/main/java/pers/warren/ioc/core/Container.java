package pers.warren.ioc.core;


import java.util.List;

public interface Container {

    Container container = new DefaultContainer();

    static Container getContainer(){
        return  container;
    }

    <T>T getBean(String name);

    <T> T getBean(Class<?> clz);

    <T> List<T> getBeans(Class<?> clz);



}
