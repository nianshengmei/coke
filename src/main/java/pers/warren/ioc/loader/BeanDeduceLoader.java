package pers.warren.ioc.loader;

import lombok.extern.slf4j.Slf4j;
import pers.warren.ioc.core.BeanDeduce;
import pers.warren.ioc.core.BeanDefinitionBuilder;
import pers.warren.ioc.core.Container;
import pers.warren.ioc.enums.BeanType;

import java.lang.reflect.Constructor;

@Slf4j
public class BeanDeduceLoader implements Loader {

    private final Container container;

    public BeanDeduceLoader() {
        this.container = Container.getContainer();
    }

    @Override
    public boolean load(Class<?> clz) {
        if (BeanDeduce.class.isAssignableFrom(clz) && (!BeanDeduce.class.equals(clz))) {
            Object o = null;
            try {
                Constructor<?> constructor = clz.getConstructor();
                o = constructor.newInstance();
                container.addBeanDefinition(BeanDefinitionBuilder.genericBeanDefinition(clz, clz.getSimpleName(), BeanType.BASE_COMPONENT, null, null).build());
            } catch (Exception e) {
                log.error("class BeanDeduce must have a constructor with no param , BeanDeduce name {}", clz.getName());
                return false;
            }
            container.addComponent(clz.getSimpleName(), o);
            return true;
        }
        return false;
    }
}
