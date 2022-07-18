package pers.warren.ioc.core;

import java.util.HashMap;
import java.util.Map;

public class Container {

    private Map<String,Object> componentMap = new HashMap<>();

    private static Container container;

    public static Container getContainer(){
        if (null == container){
            container = new Container();
        }
        return container;
    }

    public Container(){
        componentMap.put(ApplicationContext.class.getSimpleName(),new ApplicationContext());
    }


    public <T> T getBean(String name){
        return (T)componentMap.get(name);
    }
}
