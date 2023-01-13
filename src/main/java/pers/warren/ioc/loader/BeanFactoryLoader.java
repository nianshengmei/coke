package pers.warren.ioc.loader;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import pers.warren.ioc.core.BeanDefinitionBuilder;
import pers.warren.ioc.core.BeanFactory;
import pers.warren.ioc.core.Container;
import pers.warren.ioc.enums.BeanType;

import java.lang.reflect.Constructor;

@Slf4j
public class BeanFactoryLoader implements Loader{


    private final Container container;

    public BeanFactoryLoader() {
        this.container = Container.getContainer();
    }

    @Override
    public boolean load(Class<?> clz) {
        if (BeanFactory.class.isAssignableFrom(clz) && (!BeanFactory.class.equals(clz))) {
            Loader.alreadyLoadClzNames.add(clz.getTypeName());
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
                log.error("class BeanFactory must have a constructor with no param , beanFactory name {} ",beanName);
                return false;
            }
            container.addPreloadComponent(beanName, o);
            return true;
        }
        return false;
    }
}
