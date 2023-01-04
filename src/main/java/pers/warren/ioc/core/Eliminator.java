package pers.warren.ioc.core;

import java.util.*;
import java.util.stream.Collectors;

public class Eliminator {


    /**
     * 排除指定类型的bean
     */
    private final Set<Class<?>> excludeBeanClzList = new HashSet<>();

    /**
     * 排除指定名称的bean
     */
    private final Set<String> excludeBeanNameList = new HashSet<>();


    /**
     * 添加排除指定类型bean
     */
    public void addExcludeBeanClzList(final Class<?>[] clzList){
        excludeBeanClzList.addAll(Arrays.stream(clzList).collect(Collectors.toList()));
    }

    /**
     * 排除指定名称的bean
     */
    public void addExcludeBeanNameList(final String[] beanNameList){
        excludeBeanNameList.addAll(Arrays.stream(beanNameList).collect(Collectors.toList()));
    }

    /**
     * 是否排除
     */
    public boolean isExclude(Class<?> clz){
        return excludeBeanClzList.contains(clz);
    }

    /**
     * 是否排除
     */
    public boolean isExclude(String beanName){
        return excludeBeanNameList.contains(beanName);
    }
}
