package pers.warren.ioc.loader;

import cn.hutool.core.util.RandomUtil;
import pers.warren.ioc.core.ApplicationContext;
import pers.warren.ioc.core.BeanDefinitionBuilder;
import pers.warren.ioc.core.Container;
import pers.warren.ioc.enums.BeanType;

import java.lang.reflect.Constructor;

/**
 * 预加载coke 上下文 以及 预加载指示器
 *
 * @author warren
 */
public class ContextLoader implements Loader {

    /**
     * 容器
     */
    private final Container container;

    public ContextLoader() {
        this.container = Container.getContainer();
    }

    @Override
    public boolean load(Class<?> clz) {
        if (ApplicationContext.class.isAssignableFrom(clz) && (!container.hasEqualComponent(clz))) {
            Object o = null;
            final String prefixName = clz.getSimpleName();
            String beanName = clz.getSimpleName();
            while (container.containsBeanDefinition(beanName)) {
                beanName = prefixName + "@" + RandomUtil.randomString(6);
            }
            try {
                Constructor<?> constructor = clz.getConstructor();
                o = constructor.newInstance();
                container.addBeanDefinition(BeanDefinitionBuilder.genericBeanDefinition(clz, beanName, BeanType.CONTEXT, null, null).build());
            } catch (Exception e) {
                throw new RuntimeException("class ApplicationContext must have a constructor with no param , " + beanName);
            }
            container.addComponent(beanName, o);
            return true;
        }
        return false;
    }
}
