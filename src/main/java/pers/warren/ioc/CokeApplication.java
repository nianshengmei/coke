package pers.warren.ioc;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import pers.warren.ioc.annotation.Autowired;
import pers.warren.ioc.core.*;
import pers.warren.ioc.enums.BeanType;
import pers.warren.ioc.handler.CokePostHandler;
import pers.warren.ioc.handler.CokePropertiesHandler;
import pers.warren.ioc.util.InjectUtil;
import pers.warren.ioc.util.ScanUtil;

import javax.annotation.Resource;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 容器的启动辅助类
 */

@Slf4j
public class CokeApplication {

    /**
     * 所有符合扫描规则下的类的集合
     */
    private static Set<Class<?>> clzSet;

    /**
     * 启动开始时间 -用于计算容器启动耗时
     */
    private static long startTimeMills;

    public static ApplicationContext run(Class<?> clz, String[] args) {
        start();      //IOC启动前置方法,包括打印banner

        clzSet = ScanUtil.scan();   //包扫描

        loadContext();       //加载ApplicationContext

        CokePropertiesHandler.read();   //读取配置文件

        log.info("scan java and resource files ok, cost {} ms !", System.currentTimeMillis() - startTimeMills);

        loadBasicComponent();       //加载基础组件 、 包括BeanRegister初始化，BeanFactory初始化，BeanPostProcessor初始化

        loadConfiguration();         //扫描需要初始化的Bean生成BeanDefinition

        loadBean();                  //初始化Bean

        injectProperties();          //注入配置文件

        injectField();               //注入Bean

        end();                       //容器启动后置方法

        return Container.getContainer().applicationContext();
    }

    /**
     * 加载上下文
     */
    protected static void loadContext() {
        Container container = Container.getContainer();
        for (Class<?> aClass : clzSet) {
            if (ApplicationContext.class.isAssignableFrom(aClass) && (!container.hasEqualComponent(aClass))) {
                Object o = null;
                try {
                    Constructor<?> constructor = aClass.getConstructor();
                    o = constructor.newInstance();
                    container.addBeanDefinition(BeanDefinitionBuilder.genericBeanDefinition(aClass, aClass.getSimpleName(), BeanType.CONTEXT, null, null).build());
                } catch (Exception e) {
                    throw new RuntimeException("class ApplicationContext must have a constructor with no param , " + aClass.getName());
                }
                container.addComponent(aClass.getSimpleName(), o);
            }
        }

    }

    protected static void loadBasicComponent() {
        Container container = Container.getContainer();
        for (Class<?> aClass : clzSet) {
            boolean flag = false;
            if (BeanPostProcessor.class.isAssignableFrom(aClass) && (!BeanPostProcessor.class.equals(aClass))) {
                Object o = null;
                try {
                    Constructor<?> constructor = aClass.getConstructor();
                    o = constructor.newInstance();
                    flag = true;
                } catch (Exception e) {
                    throw new RuntimeException("class BeanPostProcessor must have a constructor with no param , " + aClass.getName());
                }
                container.addComponent(aClass.getSimpleName(), o);
            }

            if (BeanFactory.class.isAssignableFrom(aClass) && (!BeanFactory.class.equals(aClass))) {
                Object o = null;
                try {
                    Constructor<?> constructor = aClass.getConstructor();
                    o = constructor.newInstance();
                    flag = true;
                } catch (Exception e) {
                    throw new RuntimeException("class BeanFactory must have a constructor with no param , " + aClass.getName());
                }
                container.addComponent(aClass.getSimpleName(), o);
            }

            if (BeanRegister.class.isAssignableFrom(aClass) && (!BeanRegister.class.equals(aClass))) {
                Object o = null;
                try {
                    Constructor<?> constructor = aClass.getConstructor();
                    o = constructor.newInstance();
                    flag = true;
                } catch (Exception e) {
                    throw new RuntimeException("class BeanRegister must have a constructor with no param , " + aClass.getName());
                }
                container.addComponent(aClass.getSimpleName(), o);
            }

            List<ApplicationContext> contexts = container.getBeans(ApplicationContext.class);
            for (ApplicationContext context : contexts) {
                Class<?>[] classes = context.preloadBasicComponent();
                for (Class<?> clz : classes) {
                    if (clz.isAssignableFrom(aClass) && (!clz.equals(aClass))) {
                        Object o = null;
                        try {
                            Constructor<?> constructor = null;
                            try {
                                constructor = aClass.getConstructor();
                            } catch (Exception e) {
                                Constructor<?>[] constructors = aClass.getConstructors();
                                constructor = constructors[0];
                            }
                            Class<?>[] parameterTypes = constructor.getParameterTypes();
                            Object[] paramArr = new Object[0];
                            if (null != parameterTypes && parameterTypes.length > 0) {
                                paramArr = new Object[parameterTypes.length];
                                for (int i = 0; i < parameterTypes.length; i++) {
                                    Object obj = context.getBean(parameterTypes[i]);
                                    paramArr[i] = obj;
                                }
                            }
                            o = constructor.newInstance(paramArr);
                            flag = true;
                        } catch (Exception e) {
                            throw new RuntimeException("preload component class " + clz.getTypeName() + " must have a constructor with no param , " + aClass.getName(), e);
                        }
                        container.addComponent(aClass.getSimpleName(), o);
                    }
                }
            }

            if (flag) {
                container.addBeanDefinition(BeanDefinitionBuilder.genericBeanDefinition(aClass, aClass.getSimpleName(), BeanType.BASE_COMPONENT, null, null).build());
            }
        }

    }

    private static void loadBean() {
        Container container = Container.getContainer();
        BeanType[] beanTypes = new BeanType[]{BeanType.CONFIGURATION, BeanType.COMPONENT, BeanType.SIMPLE_BEAN};
        for (BeanType beanType : beanTypes) {
            List<BeanDefinition> beanDefinitions = container.getBeanDefinitions(beanType);
            for (BeanDefinition beanDefinition : beanDefinitions) {
                if (null != container.getBean(beanDefinition.getName())) {
                    continue;
                }
                BeanFactory beanFactory = (BeanFactory) container.getBean(beanDefinition.getBeanFactoryClass());
                FactoryBean factoryBean = beanFactory.createBean(beanDefinition);
                container.addFactoryBean(beanDefinition.getName(), factoryBean);
                container.addComponent(beanDefinition.getName(), factoryBean.getObject());
            }
        }
        List<BeanPostProcessor> postProcessors = Container.getContainer().getBeans(BeanPostProcessor.class);
        for (BeanPostProcessor postProcessor : postProcessors) {
            postProcessor.postProcessAfterBeanLoad(container);
        }
    }

    public static void injectProperties() {
        Container container = Container.getContainer();
        List<BeanDefinition> beanDefinitions = container.getBeanDefinitions(BeanType.CONFIGURATION);
        beanDefinitions.addAll(container.getBeanDefinitions(BeanType.COMPONENT));
        for (BeanDefinition beanDefinition : beanDefinitions) {
            List<ValueField> valueFiledInject = beanDefinition.getValueFiledInject();
            for (ValueField field : valueFiledInject) {
                if (null == field.getConfigValue() && null == field.getDefaultValue()) {
                    continue;
                }
                Field f = field.getField();
                Object bean = container.getBean(beanDefinition.getName());

                try {
                    Object value = InjectUtil.getDstValue(field);
                    f.setAccessible(true);
                    f.set(bean, value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    throw new RuntimeException("the value in the configuration file cannot be converted to the corresponding attribute , value field info : " + field);
                }
            }
        }
    }

    public static void injectField() {
        Container container = Container.getContainer();
        List<BeanDefinition> beanDefinitions = container.getBeanDefinitions(BeanType.CONFIGURATION);
        beanDefinitions.addAll(container.getBeanDefinitions(BeanType.COMPONENT));
        for (BeanDefinition beanDefinition : beanDefinitions) {
            List<Field> autowiredFieldList = beanDefinition.getAutowiredFieldInject();
            Object be = container.getBean(beanDefinition.getName());
            Object[] beans = new Object[]{be};
            if (beanDefinition.isProxy()) {
                beans = new Object[]{be, getBean(beanDefinition.getName(), true)};
            }
            for (Object bean : beans) {

                for (Field field : autowiredFieldList) {
                    String name = field.getName();
                    Autowired annotation = field.getAnnotation(Autowired.class);
                    Object b = null;
                    if (StrUtil.isNotEmpty(annotation.value())) {
                        name = annotation.value();
                        b = getBean(name, beanDefinition.isProxy());
                        if (null == b) {
                            throw new RuntimeException("without bean autowired named :" + name
                                    + "  , source bean" + beanDefinition.getName() + " ,Class name " + beanDefinition.getClz().getName()
                            );
                        }

                    } else {
                        b = getBean(field.getType(), beanDefinition.isProxy());
                        if (null == b) {
                            throw new RuntimeException("no bean type autowired :" + field.getType().getName()
                                    + "  , source bean" + beanDefinition.getName() + " ,Class name " + beanDefinition.getClz().getName()
                            );
                        }
                    }
                    try {
                        field.setAccessible(true);
                        field.set(bean, b);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("no bean type autowired :" + field.getType().getName()
                                + "  , source bean" + beanDefinition.getName() + " ,Class name " + beanDefinition.getClz().getName()
                        );
                    }
                }

                List<Field> resourceFieldList = beanDefinition.getResourceFieldInject();
                bean = container.getBean(beanDefinition.getName());
                for (Field field : resourceFieldList) {
                    String name = field.getName();
                    Resource annotation = field.getAnnotation(Resource.class);
                    Object b = null;
                    if (StrUtil.isNotEmpty(annotation.name())) {
                        name = annotation.name();
                        b = getBean(name, beanDefinition.isProxy());
                        if (null == b) {
                            throw new RuntimeException("without bean autowired named :" + name
                                    + "  , source bean" + beanDefinition.getName() + " ,Class name " + beanDefinition.getClz().getName()
                            );
                        }

                    } else {
                        b = getBean(field.getType(), beanDefinition.isProxy());
                        if (null == b) {
                            throw new RuntimeException("no bean type autowired :" + field.getType().getName()
                                    + "  , source bean" + beanDefinition.getName() + " ,Class name " + beanDefinition.getClz().getName()
                            );
                        }
                    }
                    try {
                        field.setAccessible(true);
                        field.set(bean, b);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("no bean type autowired :" + field.getType().getName()
                                + "  , source bean" + beanDefinition.getName() + " ,Class name " + beanDefinition.getClz().getName()
                        );
                    }
                }
            }

        }
    }


    /**
     * 启动开始
     */
    private static void start() {
        startTimeMills = System.currentTimeMillis();
        printBanner();
    }

    /**
     * 打印banner
     */
    private static void printBanner() {
        System.out.println(ResourceUtil.readUtf8Str("banner.txt"));
    }

    /**
     * 启动完成的后置操作
     */
    private static void end() {
        postHandlerRun();
        log.info("coke start ok! cost = {} ms !", System.currentTimeMillis() - startTimeMills);
    }

    /**
     * 加载配置类
     */
    private static void loadConfiguration() {
        Container container = Container.getContainer();
        List<BeanRegister> beanRegisters = container.getBeans(BeanRegister.class);
        for (Class<?> clz : clzSet) {
            AnnotationMetadata metadata = AnnotationMetadata.metadata(clz);
            for (BeanRegister beanRegister : beanRegisters) {
                beanRegister.initialization(metadata, container);
            }
        }
    }

    private static void postHandlerRun() {
        Container container = Container.getContainer();
        List<CokePostHandler> postHandlers = container.getBeans(CokePostHandler.class);
        if (CollUtil.isNotEmpty(postHandlers)) {
            log.info("coke start cost {} ms before post handler run !", System.currentTimeMillis() - startTimeMills);
        }
        for (CokePostHandler postHandler : postHandlers) {
            postHandler.run();
        }
    }

    private static Object getBean(String name, boolean proxy) {
        Container container = Container.getContainer();
        ApplicationContext proxyApplicationContext = container.getBean("ProxyApplicationContext");
        if (!proxy) {
            return container.getBean(name);
        }
        return proxyApplicationContext.getProxyBean(name);
    }

    private static Object getBean(Class<?> clz, boolean proxy) {
        Container container = Container.getContainer();
        ApplicationContext proxyApplicationContext = container.getBean("ProxyApplicationContext");
        if (!proxy) {
            return container.getBean(clz);
        }
        return proxyApplicationContext.getProxyBean(clz);
    }
}

