package pers.warren.ioc;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import pers.warren.ioc.annotation.Autowired;
import pers.warren.ioc.core.*;
import pers.warren.ioc.enums.BeanType;
import pers.warren.ioc.handler.CokePostHandler;
import pers.warren.ioc.loader.Loader;
import pers.warren.ioc.util.InjectUtil;
import pers.warren.ioc.util.ScanUtil;

import javax.annotation.Resource;
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

        Loader.loadConfigEnvironment();   //读取配置文件

        log.info("scan java and resource files ok, cost {} ms !", System.currentTimeMillis() - startTimeMills);

        Loader.preload(clzSet);   //预加载

        initBeanDefinition();         //扫描需要初始化的Bean生成BeanDefinition

        componentPostProcessorBefore();

        loadBean();                  //初始化Bean

        injectProperties();          //注入配置文件

        injectField();               //注入Bean

        end();                       //容器启动后置方法

        return Container.getContainer().applicationContext();
    }

    private static void componentPostProcessorBefore() {
        Container container = Container.getContainer();
        Collection<BeanDefinition> beanDefinitions = container.getBeanDefinitions();
        List<BeanPostProcessor> beanPostProcessors = container.getBeans(BeanPostProcessor.class);
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            for (BeanDefinition beanDefinition : beanDefinitions) {
                beanPostProcessor.postProcessBeforeInitialization(beanDefinition, beanDefinition.getRegister());
            }
        }

        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            for (BeanDefinition beanDefinition : beanDefinitions) {
                beanPostProcessor.postProcessAfterBeforeProcessor(beanDefinition, beanDefinition.getRegister());
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
                FactoryBean factoryBean = null;
                try {
                    factoryBean = beanFactory.createBean(beanDefinition);
                }catch (Exception e){
                    e.printStackTrace();
                }
//                container.addFactoryBean(beanDefinition.getName(), factoryBean);
                container.addComponent(beanDefinition.getName(), factoryBean.getObject());
            }
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
                beans = new Object[]{be, Container.getContainer().getBean(beanDefinition.getName())};
            }
            for (Object bean : beans) {

                for (Field field : autowiredFieldList) {
                    String name = field.getName();
                    Autowired annotation = field.getAnnotation(Autowired.class);
                    Object b = null;
                    if (StrUtil.isNotEmpty(annotation.value())) {
                        name = annotation.value();
                        b = Container.getContainer().getBean(name);
                        if (null == b) {
                            throw new RuntimeException("without bean autowired named :" + name
                                    + "  , source bean" + beanDefinition.getName() + " ,Class name " + beanDefinition.getClz().getName()
                            );
                        }

                    } else {
                        b = Container.getContainer().getBean(field.getType());
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
                        b = Container.getContainer().getBean(name);
                        if (null == b) {
                            throw new RuntimeException("without bean autowired named :" + name
                                    + "  , source bean" + beanDefinition.getName() + " ,Class name " + beanDefinition.getClz().getName()
                            );
                        }

                    } else {
                        b = Container.getContainer().getBean(field.getType());
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
    private static void initBeanDefinition() {
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


}

