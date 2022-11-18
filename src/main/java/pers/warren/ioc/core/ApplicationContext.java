package pers.warren.ioc.core;

import java.util.*;

/**
 * 容器操作上下文
 *
 * @author warren
 * @since jdk1.8
 */
public class ApplicationContext implements BeanDefinitionRegistry ,Environment{

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
     * 获取bean
     */
    public <T> T getBean(Class<T> clz) {
        return Container.getContainer().getBean(clz);
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

    /**
     * 判断当前运行时是否web环境
     */
    public boolean isWebEnvironment(){
        return Container.getContainer().isWebEnvironment();
    }
}
