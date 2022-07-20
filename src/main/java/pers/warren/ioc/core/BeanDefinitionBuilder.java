package pers.warren.ioc.core;

import pers.warren.ioc.enums.BeanType;

public class BeanDefinitionBuilder {

    private BeanDefinition beanDefinition;

    public BeanDefinitionBuilder() {
        this.beanDefinition = new BeanDefinition();
    }

    public static BeanDefinitionBuilder genericBeanDefinition(Class<?> beanClass, String name){
        BeanDefinitionBuilder builder = new BeanDefinitionBuilder();
        builder.beanDefinition.setClz(beanClass);
        builder.beanDefinition.setName(name);
        builder.beanDefinition.setBeanType(BeanType.SIMPLE_BEAN);
        return builder;
    }

    public static BeanDefinitionBuilder genericBeanDefinition(Class<?> beanClass,String name,BeanType beanType){
        BeanDefinitionBuilder builder = new BeanDefinitionBuilder();
        builder.beanDefinition.setClz(beanClass);
        builder.beanDefinition.setName(name);
        builder.beanDefinition.setBeanType(beanType);
        return builder;
    }

    public BeanDefinition build(){
        return beanDefinition;
    }


}
