package pers.warren.ioc.core;

import lombok.extern.slf4j.Slf4j;
import pers.warren.ioc.annotation.Autowired;
import pers.warren.ioc.ec.NoMatchBeanException;
import pers.warren.ioc.util.ReflectUtil;

import javax.annotation.Resource;
import java.lang.reflect.Constructor;
import java.util.List;

/**
 * needcoke-ioc默认的FactoryBean
 * <p>
 * 用于Configuration 和Component的创建
 *
 * @author warren
 * @since jdk1.8
 */
@Slf4j
public class DefaultFactoryBean implements FactoryBean{

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
        List<String> methodParamNames = ReflectUtil.getParameterNames(constructor);
        Class[] parameterTypes = constructor.getParameterTypes();
        try {
            Object[] params = new Object[methodParamNames.size()];
            for (int i = 0; i < methodParamNames.size(); i++) {
                Object bean = Container.getContainer().getBean(methodParamNames.get(i));
                if(bean != null && !bean.getClass().getTypeName().equals(parameterTypes[i].getTypeName())){
                    bean = null;
                }
                if (null == bean) {
                    bean =  Container.getContainer().getBean(parameterTypes[i]);
                }
                if (null == bean) {
                    BeanDefinition bdByName = Container.getContainer().getBeanDefinition(methodParamNames.get(i));
                    BeanDefinition bdByType = Container.getContainer().getBeanDefinition(parameterTypes[i]);
                    if (null == bdByName && null == bdByType) {
                        throw new NoMatchBeanException("can't find param in container named : " + methodParamNames.get(i));
                    }
                    Object obj = null;
                    if (null != bdByName) {
                        FactoryBean factoryBean = currentBeanFactory.createBean(bdByName);
                        if(factoryBean.getType().getTypeName().equals(parameterTypes[i].getTypeName())){
                            factoryBean = currentBeanFactory.createBean(bdByType);
                        }
                        obj = factoryBean.getObject();
                        Container.getContainer().addComponent(factoryBean.getName(), obj);
                    } else {
                        FactoryBean factoryBean = currentBeanFactory.createBean(bdByType);
                        obj = factoryBean.getObject();
                        Container.getContainer().addComponent(bdByType.getName(), obj);
                    }
                    bean = obj;
                    if (null == bean) {
                        bean = Container.getContainer().getBean(parameterTypes[i]);
                    }
                }
                params[i] = bean;
            }
            Object o = constructor.newInstance(params);
            return o;
        } catch (Exception e) {
            log.error("bean name {}, need inject a bean name like {} , type = {}",beanDefinition.getName(),methodParamNames,parameterTypes);
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

    @Override
    public String getName() {
        return beanDefinition.getName();
    }

    /**
     * 是否单例
     */
    @Override
    public Boolean isSingleton() {
        return beanDefinition.isSingleton();
    }
}
