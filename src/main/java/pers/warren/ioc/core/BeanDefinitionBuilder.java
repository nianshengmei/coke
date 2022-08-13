package pers.warren.ioc.core;

import pers.warren.ioc.enums.BeanType;

import java.lang.annotation.Annotation;

/**
 * beanDefinition创建工具
 *
 * @author warren
 * @since jdk1.8
 */
public class BeanDefinitionBuilder {

    private BeanDefinition beanDefinition;

    public BeanDefinitionBuilder() {
        this.beanDefinition = new BeanDefinition();
    }

    public static BeanDefinitionBuilder genericBeanDefinition(Class<?> beanClass, String name, Object function, String invokeSource) {
        BeanDefinitionBuilder builder = new BeanDefinitionBuilder();
        builder.beanDefinition.setClz(beanClass);
        builder.beanDefinition.setName(name);
        builder.beanDefinition.setInvokeSource(invokeSource);
        builder.beanDefinition.setInvokeFunction(function);
        builder.beanDefinition.setBeanType(BeanType.SIMPLE_BEAN);
        builder.beanDefinition.setFactoryBeanClass(DefaultFactoryBean.class);
        builder.beanDefinition.setBeanFactoryClass(DefaultBeanFactory.class);
        return builder;
    }

    public static BeanDefinitionBuilder genericBeanDefinition(Class<?> beanClass, String name, BeanType beanType, Object function, String invokeSource) {
        BeanDefinitionBuilder builder = new BeanDefinitionBuilder();
        builder.beanDefinition.setClz(beanClass);
        builder.beanDefinition.setName(name);
        builder.beanDefinition.setInvokeFunction(function);
        builder.beanDefinition.setInvokeSource(invokeSource);
        builder.beanDefinition.setBeanType(beanType);
        builder.beanDefinition.setFactoryBeanClass(DefaultFactoryBean.class);
        builder.beanDefinition.setBeanFactoryClass(DefaultBeanFactory.class);
        return builder;
    }

    public BeanDefinitionBuilder setFactoryBeanType(Class<?> factoryBeanClass) {
        this.beanDefinition.setFactoryBeanClass(factoryBeanClass);
        return this;
    }

    public BeanDefinitionBuilder setBeanFactoryType(Class<?> beanFactoryType) {
        this.beanDefinition.setFactoryBeanClass(beanFactoryType);
        return this;
    }

    public BeanDefinitionBuilder setRegister(BeanRegister register){
        this.beanDefinition.setRegister(register);
        return this;
    }

    public BeanDefinitionBuilder setScanByAnnotationClass(Class<?> annotationClass){
        this.beanDefinition.setScanByAnnotationClass(annotationClass);
        return this;
    }

    public BeanDefinitionBuilder setScanByAnnotation(Annotation annotation){
        this.beanDefinition.setScanByAnnotation(annotation);
        return this;
    }

    public BeanDefinition build() {
        return beanDefinition;
    }


}
