package pers.warren.ioc.loader;

import lombok.extern.slf4j.Slf4j;
import pers.warren.ioc.core.BeanDefinitionBuilder;
import pers.warren.ioc.core.Container;
import pers.warren.ioc.core.PreLoad;
import pers.warren.ioc.enums.BeanType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 预加载接口加载
 *
 * @author warren
 */
@Slf4j
public class PreLoadLoader implements Loader {

    private final Container container;

    public PreLoadLoader() {
        this.container = Container.getContainer();
    }

    private static List<Class<?>> preLoadClasses = new ArrayList<>();

    @Override
    public boolean load(Class<?> clz) {
        if (PreLoad.class.isAssignableFrom(clz) && (!clz.equals(PreLoad.class))) {
            try {
                Object preLoad = container.getBean(clz);
                if (null == preLoad) {
                    preLoad = clz.getConstructor().newInstance();
                }
                Method method = clz.getMethod("preloadBasicComponentClass");
                Class<?>[] preClzArray = (Class<?>[]) method.invoke(preLoad);
                preLoadClasses.addAll(Arrays.asList(preClzArray));
            } catch (Exception e) {
                log.error("coke 's PreLoad must use constructor with no parameter !  error init class {}", clz.getTypeName());
            }
            Loader.alreadyLoadClzNames.add(clz.getTypeName());
        }
        return true;
    }

    public boolean preLoad(Class<?> clz) {
        for (Class<?> aClass : preLoadClasses) {
            if (aClass.isAssignableFrom(clz) && !clz.isInterface() && !Modifier.isAbstract(clz.getModifiers())) {
                Object o = null;
                try {
                    Constructor<?> constructor = null;
                    try {
                        constructor = clz.getConstructor();
                    } catch (Exception e) {
                        Constructor<?>[] constructors = clz.getConstructors();
                        constructor = constructors[0];
                    }
                    Class<?>[] parameterTypes = constructor.getParameterTypes();
                    Object[] paramArr = new Object[0];
                    if (null != parameterTypes && parameterTypes.length > 0) {
                        paramArr = new Object[parameterTypes.length];
                        for (int i = 0; i < parameterTypes.length; i++) {
                            Object obj = container.getBean(parameterTypes[i]);
                            paramArr[i] = obj;
                        }
                    }
                    o = constructor.newInstance(paramArr);
                    container.addBeanDefinition(BeanDefinitionBuilder.genericBeanDefinition(clz, clz.getSimpleName(), BeanType.BASE_COMPONENT, null, null).build());
                } catch (Exception e) {
                    throw new RuntimeException("preload component class " + clz.getTypeName() + " must have a constructor with no param , " + clz.getName(), e);
                }
                container.addComponent(clz.getSimpleName(), o);
            }
        }
        return true;
    }
}
