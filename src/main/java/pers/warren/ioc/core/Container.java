package pers.warren.ioc.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import pers.warren.ioc.enums.BeanType;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class Container implements BeanDefinitionRegistry {

    private Map<String, Object> componentMap = new HashMap<>();

    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    private Map<String, FactoryBean> factoryBeanMap = new HashMap<>();


    public void addFactoryBean(String name, FactoryBean factoryBean) {
        factoryBeanMap.put(name, factoryBean);
    }

    public void addComponent(String name, Object o) {
        componentMap.put(name, o);
    }

    private static Container container;

    private final Map<String,List<InputStream>> propertiesIsMap = new HashMap<>();

    public Map<String,List<InputStream>> getPropertiesIsMap(){
        return propertiesIsMap;
    }

    public static Container getContainer() {
        if (null == container) {
            container = new Container();
        }
        return container;
    }

    public ApplicationContext applicationContext(){
        return this.getBean(ApplicationContext.class);
    }

    private Container() {

    }

    public List<BeanDefinition> getBeanDefinitions(BeanType beanType) {
        return beanDefinitionMap.values().stream().filter(d -> beanType == d.getBeanType()).collect(Collectors.toList());
    }


    public <T> T getBean(String name) {
        return (T) componentMap.get(name);
    }

    public <T> T getBean(Class<T> clz) {
        List<T> beans = getBeans(clz);
        return CollUtil.isNotEmpty(beans) ? beans.get(0) : null;
    }

    public <T> List<T> getBeans(Class<T> clz) {
        Collection<Object> values = componentMap.values();
        List<T> tList = new ArrayList<>();
        for (Object bean : values) {
            if(clz.isAssignableFrom(bean.getClass())){
                tList.add((T)bean);
            }
        }
        return tList;
    }

    @Override
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
        if (StrUtil.isNotEmpty(name) && null != beanDefinition) {
            this.beanDefinitionMap.put(name, beanDefinition);
        }
    }

    @Override
    public void removeBeanDefinition(String name) {
        this.beanDefinitionMap.remove(name);
    }

    @Override
    public BeanDefinition getBeanDefinition(String name) {
        return this.beanDefinitionMap.get(name);
    }

    public BeanDefinition getBeanDefinition(Class<?> clz) {
        Collection<BeanDefinition> values = beanDefinitionMap.values();
        for (BeanDefinition value : values) {
            Class<?> aClass = value.getClz();
            if(clz.isAssignableFrom(aClass)){
                return value;
            }
        }
        return null;
    }

    @Override
    public boolean containsBeanDefinition(String name) {
        return beanDefinitionMap.containsKey(name);
    }

    @Override
    public String[] getBeanDefinitionNames() {
        String[] strings = new String[beanDefinitionMap.size()];
        return beanDefinitionMap.keySet().toArray(strings);
    }

    @Override
    public int getBeanDefinitionCount() {
        return beanDefinitionMap.size();
    }

    @Override
    public boolean isBeanNameInUse(String name) {
        return beanDefinitionMap.containsKey(name) || componentMap.containsKey(name);
    }
}
