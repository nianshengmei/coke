package pers.warren.ioc.core;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import pers.warren.ioc.inject.Inject;
import java.lang.reflect.Method;


public class LazyFactoryBean implements FactoryBean{

    /**
     * beanDefinition
     */
    private BeanDefinition beanDefinition;

    /**
     * 创建该FactoryBean的工厂
     */
    private BeanFactory currentBeanFactory;

    public LazyFactoryBean(BeanDefinition beanDefinition, BeanFactory currentBeanFactory) {
        this.beanDefinition = beanDefinition;
        this.currentBeanFactory = currentBeanFactory;
    }

    @Override
    public Object getObject() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(beanDefinition.getClz());
        enhancer.setCallback(new LazyInterceptor(beanDefinition));
        return enhancer.create();
    }

    @Override
    public Class getType() {
        return beanDefinition.getClz();
    }

    @Override
    public String getName() {
        return beanDefinition.getName();
    }

    @Override
    public Boolean isSingleton() {
        return beanDefinition.isSingleton();
    }

    private static class LazyInterceptor implements MethodInterceptor {

        private Object bean;

        private BeanDefinition beanDefinition;

        public LazyInterceptor(BeanDefinition beanDefinition) {
            this.beanDefinition = beanDefinition;
        }

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            if(bean == null){
                BeanFactory beanFactory = (BeanFactory) Container.getContainer().getBean(beanDefinition.getBeanFactoryClass());
                bean = beanFactory.createBean(beanDefinition).getObject();
                Container.getContainer().getLazyBeanMap().put(beanDefinition.getName(), bean);
                Inject.injectBeanDefinitionFiled(beanDefinition);  //依赖注入
            }
            Method superMethod = beanDefinition.getClz().getMethod(method.getName(), method.getParameterTypes());
            return superMethod.invoke(bean,objects);
        }
    }
}
