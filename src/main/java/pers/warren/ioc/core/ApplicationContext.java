package pers.warren.ioc.core;

import pers.warren.ioc.handler.CokePropertiesHandler;

import java.util.*;

/**
 * 容器操作上下文
 *
 * @author warren
 * @since jdk1.8
 */
public class ApplicationContext implements BeanDefinitionRegistry {

    /**
     * 从 application.yml,application.properties中读取的配置文件信息
     * <p>
     * application.properties优先级 > application.yml
     */
    private Map<String, Object> propertiesMap;

    public ApplicationContext() {
        this.propertiesMap = new HashMap<>();
    }

    /**
     * 获取特定配置属性
     */
    public Object getProperty(String k) {
        return this.propertiesMap.get(k);
    }

    /**
     * 添加单个属性
     */
    public void addProperty(String k, Object v) {
        this.propertiesMap.put(k, v);
    }

    /**
     * 添加多个属性
     */
    public void addProperties(Map<String, Object> source) {
        this.propertiesMap.putAll(source);
    }

    /**
     * 清空配置文件
     */
    public void clearProperties() {
        this.propertiesMap = null;
    }

    public void refreshProperties() {
        CokePropertiesHandler.read();
    }

    /**
     * 注册bean定义
     */
    @Override
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
        Container.getContainer().registerBeanDefinition(name, beanDefinition);
    }

    /**
     * 移除bean定义
     */
    @Override
    public void removeBeanDefinition(String name) {
        Container.getContainer().removeBeanDefinition(name);
    }

    /**
     * 获取bean定义
     */
    @Override
    public BeanDefinition getBeanDefinition(String name) {
        return Container.getContainer().getBeanDefinition(name);
    }

    public BeanDefinition getBeanDefinition(Class<?> clz){
        return Container.getContainer().getBeanDefinition(clz);
    }

    /**
     * 包含bean定义
     */
    @Override
    public boolean containsBeanDefinition(String name) {
        return Container.getContainer().containsBeanDefinition(name);
    }

    /**
     * 获取bean定义名称
     */
    @Override
    public String[] getBeanDefinitionNames() {
        return Container.getContainer().getBeanDefinitionNames();
    }

    /**
     * 获取bean定义的数量
     */
    @Override
    public int getBeanDefinitionCount() {
        return Container.getContainer().getBeanDefinitionCount();
    }

    /**
     * bean的名称是否使用
     */
    @Override
    public boolean isBeanNameInUse(String name) {
        return Container.getContainer().containsBeanDefinition(name);
    }

    /**
     * 获取bean
     */
    public <T> T getBean(String name) {
        return Container.getContainer().getBean(name);
    }

    /**
     * 获取代理bean
     */
    public <T> T getProxyBean(String name) {
        return null;
    }

    /**
     * 获取bean
     */
    public <T> T getBean(Class<T> clz) {
        return Container.getContainer().getBean(clz);
    }

    /**
     * 获取代理bean
     */
    public <T> T getProxyBean(Class<T> clz) {
        return null;
    }
    /**
     * 获取bean
     */
    public <T> List<T> getBeans(Class<T> clz) {
        return Container.getContainer().getBeans(clz);
    }

    @Override
    public Collection<BeanWrapper> getBeanWrappers() {
        return Container.getContainer().getBeanWrappers();
    }

    public Class<?>[] preloadBasicComponentClass(){
        return new Class[0];
    }

    public Class<?>[] preloadBasicComponentAnnotationClass() {
        return new Class[0];
    }
}
