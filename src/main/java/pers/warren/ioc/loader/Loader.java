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
        for (Class<?> clz : clzSet) {

            new PreLoadLoader().load(clz);

            if (new ContextLoader().load(clz)) {
                Loader.alreadyLoadClzNames.add(clz.getTypeName());
                continue;
            }

            if (new BeanPostProcessorLoader().load(clz)) {
                Loader.alreadyLoadClzNames.add(clz.getTypeName());
                continue;
            }

            if (new BeanRegisterLoader().load(clz)) {
                Loader.alreadyLoadClzNames.add(clz.getTypeName());
                continue;
            }

            new BeanFactoryLoader().load(clz);

        }

        for (Class<?> clz : clzSet) {
            new PreLoadLoader().preLoad(clz);
        }
    }

    static void loadConfigEnvironment() {
        new PropertiesLoader().load(null);   //读取配置文件
    }
}
