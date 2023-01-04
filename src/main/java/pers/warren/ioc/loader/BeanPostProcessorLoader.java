package pers.warren.ioc.loader;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import pers.warren.ioc.core.BeanDefinitionBuilder;
import pers.warren.ioc.core.BeanPostProcessor;
import pers.warren.ioc.core.Container;
import pers.warren.ioc.enums.BeanType;

import java.lang.reflect.Constructor;

@Slf4j
public class BeanPostProcessorLoader implements Loader {

    private final Container container;

    public BeanPostProcessorLoader() {
        this.container = Container.getContainer();
    }

    @Override
    public boolean load(Class<?> clz) {
        if (BeanPostProcessor.class.isAssignableFrom(clz) && (!BeanPostProcessor.class.equals(clz))) {
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
                log.error("class BeanPostProcessor must have a constructor with no param , beanPostProcessor name {}", beanName);
                return false;
            }
            container.addComponent(beanName, o);
            return true;
        }
        return false;
    }
}
