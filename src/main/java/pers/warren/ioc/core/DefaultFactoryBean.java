package pers.warren.ioc.core;

import pers.warren.ioc.annotation.Autowired;
import pers.warren.ioc.util.ReflectUtil;

import javax.annotation.Resource;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * needcoke-ioc默认的FactoryBean
 * <p>
 * 用于Configuration 和Component的创建
 *
 * @author warren
 * @since jdk1.8
 */
public class DefaultFactoryBean implements FactoryBean {

    /**
     * beanDefinition
     */
    private BeanDefinition beanDefinition;

    /**
     * 创建该FactoryBean的工厂
     */
    private BeanFactory currentBeanFactory;

    public DefaultFactoryBean(BeanDefinition beanDefinition, BeanFactory currentBeanFactory) {
        this.beanDefinition = beanDefinition;
        this.currentBeanFactory = currentBeanFactory;
    }

    /**
     * 获取bean的对象
     */
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
        if (null == constructor) {
            throw new RuntimeException("class " + clz.getName() + " have no constructor !");
        }
        try {
            List<String> methodParamNames = ReflectUtil.getParameterNames(constructor);
            Class[] parameterTypes = constructor.getParameterTypes();
            Object[] params = new Object[methodParamNames.size()];
            for (int i = 0; i < methodParamNames.size(); i++) {
                Object bean = getBean(methodParamNames.get(i));
                if (null == bean) {
                    bean = getBean(parameterTypes[i]);
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
                        Container.getContainer().addFactoryBean(bdByType.getName(), factoryBean);
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
        }
    }

    /**
     * bean的类型
     */
    @Override
    public Class<?> getType() {
        return beanDefinition.getClz();
    }

    /**
     * 是否单例
     */
    @Override
    public Boolean isSingleton() {
        return beanDefinition.isSingleton();
    }

    private static Object getBean(String name) {
        Container container = Container.getContainer();
        BeanDefinition bdf = container.getBeanDefinition(name);
        return getBean(name,  bdf.isProxy());
    }

    private static Object getBean(String name, boolean proxy) {
        Container container = Container.getContainer();
        ApplicationContext proxyApplicationContext = container.getBean("ProxyApplicationContext");
        if (!proxy) {
            return container.getBean(name);
        }
        return proxyApplicationContext.getProxyBean(name);
    }

    private static Object getBean(Class<?> clz) {
        Container container = Container.getContainer();
        BeanDefinition bdf = container.getBeanDefinition(clz);
        return getBean(clz, bdf.isProxy());
    }

    private static Object getBean(Class<?> clz, boolean proxy) {
        Container container = Container.getContainer();
        ApplicationContext proxyApplicationContext = container.getBean("ProxyApplicationContext");
        if (!proxy) {
            return container.getBean(clz);
        }
        return proxyApplicationContext.getProxyBean(clz);
    }

}
