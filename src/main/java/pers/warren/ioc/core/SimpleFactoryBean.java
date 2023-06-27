package pers.warren.ioc.core;

import pers.warren.ioc.util.ReflectUtil;

import java.lang.reflect.Method;

/**
 * 简单Bean的FactoryBean
 *
 * @author warren
 */
public class SimpleFactoryBean implements FactoryBean {

    /**
     * beanDefinition
     */
    private BeanDefinition beanDefinition;

    /**
     * 创建FactoryBean的BeanFactory
     */
    private BeanFactory currentBeanFactory;

    public SimpleFactoryBean(BeanDefinition beanDefinition, BeanFactory currentBeanFactory) {
        this.beanDefinition = beanDefinition;
        this.currentBeanFactory = currentBeanFactory;
    }


    @Override
    public Object getObject() {
        Container container = Container.getContainer();
        //如果bean的类型是单例，那么尝试从容器中获取bean
        if (beanDefinition.isSingleton()) {
            Object bean = container.getBean(beanDefinition.getName());
            if (null != bean) {
                return bean;
            }
        }
        String invokeSource = beanDefinition.getInvokeSource();
        Object sourceBean = container.getBean(invokeSource);
        if (null == sourceBean) {
            BeanDefinition sourceBeanDefinition = container.getBeanDefinition(invokeSource);
            FactoryBean factoryBean = currentBeanFactory.createBean(sourceBeanDefinition);
            Object object = factoryBean.getObject();
            Container.getContainer().addComponent(sourceBeanDefinition.getName(), object);
            sourceBean = object;
        }
        Method invokeFunction = (Method) beanDefinition.getInvokeFunction();
        try {
            String[] parameterNames = ReflectUtil.getParameterNames(invokeFunction);
            Class<?>[] parameterTypes = invokeFunction.getParameterTypes();
            Object[] params = new Object[parameterNames.length];
            for (int i = 0; i < parameterNames.length; i++) {
                Object b = Container.getContainer().getBean(parameterNames[i]);
                if (null == b) {
                    b = Container.getContainer().getBean(parameterTypes[i]);
                }
                if (null == b) {
                    BeanDefinition bdByName = Container.getContainer().getBeanDefinition(parameterNames[i]);
                    BeanDefinition bdByType = Container.getContainer().getBeanDefinition(parameterTypes[i]);
                    if (null == bdByName && null == bdByType) {
                        throw new RuntimeException(String.format("%s can't find param in container named : %s", invokeSource, parameterNames[i]));
                    }
                    if (null != bdByName) {
                        FactoryBean factoryBean = currentBeanFactory.createBean(bdByName);
                        Container.getContainer().addComponent(bdByName.getName(), factoryBean.getObject());
                    } else {
                        FactoryBean factoryBean = currentBeanFactory.createBean(bdByType);
                        Container.getContainer().addComponent(bdByType.getName(), factoryBean.getObject());
                    }
                    b = Container.getContainer().getBean(parameterNames[i]);
                    if (null == b) {
                        b = Container.getContainer().getBean(parameterTypes[i]);
                    }
                }
                params[i] = b;
            }
            return invokeFunction.invoke(sourceBean, params);
        } catch (Exception e) {
            throw new RuntimeException("invoke method exception , bean name :" + beanDefinition.getName(),e);
        }
    }

    @Override
    public String getName() {
        return beanDefinition.getName();
    }

    @Override
    public Class<?> getType() {
        return beanDefinition.getClz();
    }

    @Override
    public Boolean isSingleton() {
        return beanDefinition.isSingleton();
    }
}
