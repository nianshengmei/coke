package pers.warren.ioc.core;

import pers.warren.ioc.handler.CokePropertiesHandler;
import java.util.HashMap;
import java.util.Map;

public class ApplicationContext implements BeanDefinitionRegistry{

    /**
     * 从 application.yml,application.properties中读取的配置文件信息
     * <p>
     * application.properties优先级 > application.yml
     */
    private Map<String, Object> propertiesMap;

    public ApplicationContext() {
        this.propertiesMap = new HashMap<>();
    }

    public Object getProperty(String k){
        return this.propertiesMap.get(k);
    }

    /**
     * 添加单个属性
     */
    public void addProperty(String k,Object v){
        this.propertiesMap.put(k,v);
    }
    /**
     * 添加多个属性
     */
    public void addProperties(Map<String, Object> source){
        this.propertiesMap.putAll(source);
    }

    /**
     * 清空配置文件
     */
    public void clearProperties(){
        this.propertiesMap = null;
    }

    public void refreshProperties(){
        CokePropertiesHandler.read();
    }


    @Override
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
        Container.getContainer().registerBeanDefinition(name,beanDefinition);
    }

    @Override
    public void removeBeanDefinition(String name) {
        Container.getContainer().removeBeanDefinition(name);
    }

    @Override
    public BeanDefinition getBeanDefinition(String name) {
        return Container.getContainer().getBeanDefinition(name);
    }

    @Override
    public boolean containsBeanDefinition(String name) {
        return Container.getContainer().containsBeanDefinition(name);
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return Container.getContainer().getBeanDefinitionNames();
    }

    @Override
    public int getBeanDefinitionCount() {
        return Container.getContainer().getBeanDefinitionCount();
    }

    @Override
    public boolean isBeanNameInUse(String name) {
        return Container.getContainer().containsBeanDefinition(name);
    }
}
