package pers.warren.ioc.loader;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import pers.warren.ioc.core.BeanDefinitionBuilder;
import pers.warren.ioc.core.BeanRegister;
import pers.warren.ioc.core.Container;
import pers.warren.ioc.enums.BeanType;

import java.lang.reflect.Constructor;

/**
 * Bean注册器加载器，负责将BeanRegister的实现类注册倒容器中
 *
 * @author warren
 * @since 1.0.0
 */
@Slf4j
public class BeanRegisterLoader implements Loader{

    private final Container container;

    public BeanRegisterLoader() {
        this.container = Container.getContainer();
    }

    @Override
    public boolean load(Class<?> clz) {
        if (BeanRegister.class.isAssignableFrom(clz) && (!BeanRegister.class.equals(clz))) {
            Object o = null;
            final String prefixName = clz.getSimpleName();
            String beanName = clz.getSimpleName();
            while (container.containsBeanDefinition(beanName)) {
                beanName = prefixName + "@" + RandomUtil.randomString(6);
            }
            try {
                Constructor<?> constructor = clz.getConstructor();
                o = constructor.newInstance();
                container.addBeanDefinition(BeanDefinitionBuilder.genericBeanDefinition(clz, beanName, BeanType.BASE_COMPONENT, null, null).build());
            } catch (Exception e) {
                log.error("class BeanRegister must have a constructor with no param , BeanRegister name {}" + clz.getName());
                return false;
            }
            container.addPreloadComponent(beanName, o);
            return true;
        }
        return false;
    }
}
