package pers.warren.ioc.core;

import pers.warren.ioc.annotation.Autowired;
import pers.warren.ioc.util.ReflectUtil;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class DefaultFactoryBean implements FactoryBean {

    private BeanDefinition beanDefinition;

    private BeanFactory currentBeanFactory;

    public DefaultFactoryBean(BeanDefinition beanDefinition, BeanFactory currentBeanFactory) {
        this.beanDefinition = beanDefinition;
        this.currentBeanFactory = currentBeanFactory;
    }

    @Override
    public Object getObject() {
        if (beanDefinition.isSingleton()) {
            Object beanDef = Container.getContainer().getBean(beanDefinition.getName());
            if (null != beanDef) {
                return beanDef;
            }
        }
        Constructor constructor = null;
        Class<?> clz = beanDefinition.getClz();
        try {
            constructor = clz.getConstructor();
        } catch (Exception e) {
        }
        Constructor<?>[] constructors = clz.getConstructors();
        for (Constructor<?> c : constructors) {
            if (null != c.getAnnotation(Autowired.class)) {
                constructor = c;
                break;
            }
            if (null != c.getAnnotation(Resource.class)) {
                constructor = c;
                break;
            }
            if (null == constructor) {
                constructor = c;
            }
        }
        try {
            List<String> methodParamNames = ReflectUtil.getParameterNames(constructor);
            Class[] parameterTypes = constructor.getParameterTypes();
            Object[] params = new Object[methodParamNames.size()];
            for (int i = 0; i < methodParamNames.size(); i++) {
                Object bean = Container.getContainer().getBean(methodParamNames.get(i));
                if (null == bean) {
                    bean = Container.getContainer().getBean(parameterTypes[i]);
                }
                if (null == bean) {
                    BeanDefinition bdByName = Container.getContainer().getBeanDefinition(methodParamNames.get(i));
                    BeanDefinition bdByType = Container.getContainer().getBeanDefinition(parameterTypes[i]);
                    if (null == bdByName && null == bdByType) {
                        throw new RuntimeException("can't find param in container named : " + methodParamNames.get(i));
                    }
                    if (null != bdByName) {
                        FactoryBean factoryBean = currentBeanFactory.createBean(bdByName);
                        Container.getContainer().addComponent(bdByName.getName(), factoryBean.getObject());
                        Container.getContainer().addFactoryBean(bdByName.getName(), factoryBean);
                    } else {
                        FactoryBean factoryBean = currentBeanFactory.createBean(bdByType);
                        Container.getContainer().addComponent(bdByType.getName(), factoryBean.getObject());
                        Container.getContainer().addFactoryBean(bdByName.getName(), factoryBean);
                    }
                    bean = Container.getContainer().getBean(methodParamNames.get(i));
                    if (null == bean) {
                        bean = Container.getContainer().getBean(parameterTypes[i]);
                    }
                }
                params[i] = bean;
            }
            return constructor.newInstance(params);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
