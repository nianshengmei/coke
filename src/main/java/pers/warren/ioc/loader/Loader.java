package pers.warren.ioc.loader;

import java.util.HashSet;
import java.util.Set;

/**
 * coke预加载器
 *
 * @author warren
 */
public interface Loader {

    String APPLICATION_CONTEXT_NAME = "applicationContext";

    Set<String> alreadyLoadClzNames = new HashSet<>();

    boolean load(Class<?> clz);

    /**
     * 预加载
     *
     */
    static void preload(Set<Class<?>> clzSet) {
        ContextLoader contextLoader = new ContextLoader();
        BeanPostProcessorLoader beanPostProcessorLoader = new BeanPostProcessorLoader();
        BeanRegisterLoader beanRegisterLoader = new BeanRegisterLoader();
        BeanFactoryLoader beanFactoryLoader = new BeanFactoryLoader();
        PreLoadLoader preLoadLoader = new PreLoadLoader();
        BeanDeduceLoader beanDeduceLoader = new BeanDeduceLoader();
        for (Class<?> clz : clzSet) {

            new PreLoadLoader().load(clz);

            if (contextLoader.load(clz)) {
                Loader.alreadyLoadClzNames.add(clz.getTypeName());
                continue;
            }

            if (beanPostProcessorLoader.load(clz)) {
                Loader.alreadyLoadClzNames.add(clz.getTypeName());
                continue;
            }

            if (beanRegisterLoader.load(clz)) {
                Loader.alreadyLoadClzNames.add(clz.getTypeName());
                continue;
            }

            if(beanDeduceLoader.load(clz)){
                Loader.alreadyLoadClzNames.add(clz.getTypeName());
                continue;
            }
            beanFactoryLoader.load(clz);
        }

        for (Class<?> clz : clzSet) {
            preLoadLoader.preLoad(clz);
        }
    }

    static void loadConfigEnvironment() {
        new PropertiesLoader().load(null);   //读取配置文件
    }
}
