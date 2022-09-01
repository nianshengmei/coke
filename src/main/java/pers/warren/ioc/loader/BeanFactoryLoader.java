package pers.warren.ioc.loader;

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
            try {
                Constructor<?> constructor = clz.getConstructor();
                o = constructor.newInstance();
                container.addBeanDefinition(BeanDefinitionBuilder.genericBeanDefinition(clz, clz.getSimpleName(), BeanType.BASE_COMPONENT, null, null).build());
            } catch (Exception e) {
                log.error("class BeanFactory must have a constructor with no param , beanFactory name {} ", clz.getName());
                return false;
            }
            container.addComponent(clz.getSimpleName(), o);
            return true;
        }
        return false;
    }
}
