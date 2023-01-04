package pers.warren.ioc.inject;

import pers.warren.ioc.core.BeanDefinition;
import pers.warren.ioc.core.Container;
import pers.warren.ioc.ec.WarnEnum;
import pers.warren.ioc.log.CokeLogger;

import java.util.Collection;
import java.util.List;

/**
 * bean注入接口
 *
 * @author warren
 */
public interface Inject extends CokeLogger {
    Container container = Container.getContainer();

    static void injectFiled() {
        Collection<BeanDefinition> beanDefinitions = container.getBeanDefinitions();
        for (BeanDefinition beanDefinition : beanDefinitions) {
            new AutowiredInject().inject(beanDefinition);
            new ResourceInject().inject(beanDefinition);
            new ValueInject().inject(beanDefinition);
        }
    }

    /**
     * 注入业务接口
     */
    void inject(BeanDefinition beanDefinition);


    /**
     * 根据是否支持代理bean 获取bean
     * <p>
     * 仅当proxy为true ,并且满足aop环境 ,该name存在代理对象时才返回代理bean ，否则返回原bean
     *
     * @param name  非代理bean的名称
     * @param proxy 包扫描时推理得到应该获取代理对象与否
     */


    default Object getBean(String name, boolean proxy) {
        if (proxy) {
            if (container.isAopEnvironment()) {
                if (container.containsProxyBean(name)) {
                    return container.getProxyBean(name);
                } else {
                    warn(WarnEnum.BEAN_WITHOUT_PROXY_INSTANCE);
                }
            } else {
                warn(WarnEnum.NOT_AOP_ENVIRONMENT);
            }
        }
        return Container.getContainer().getBean(name);
    }

    /**
     * 根据是否支持代理bean 获取bean
     * <p>
     * 仅当proxy为true ,并且满足aop环境 ,该name存在代理对象时才返回代理bean ，否则返回原bean
     *
     * @param clz  非代理bean的类型
     * @param proxy 包扫描时推理得到应该获取代理对象与否
     */
    default Object getBean(Class<?> clz, boolean proxy) {
        if (proxy) {
            if (container.isAopEnvironment()) {
                if (container.containsProxyBean(clz)) {
                    return container.getProxyBean(clz);
                } else {
                    warn(WarnEnum.BEAN_WITHOUT_PROXY_INSTANCE);
                }
            } else {
                warn(WarnEnum.NOT_AOP_ENVIRONMENT);
            }
        }
        List<BeanDefinition> beanDefinitions = container.getBeanDefinitions(clz);
        for (BeanDefinition beanDefinition : beanDefinitions){
            if (!container.isProxyBeanDefinition(beanDefinition)) {
                return container.getBean(beanDefinition.getName());
            }
        }
        return container.getBean(clz);
    }
}
