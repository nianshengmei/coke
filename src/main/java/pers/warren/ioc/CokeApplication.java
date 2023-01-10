package pers.warren.ioc;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import lombok.extern.slf4j.Slf4j;
import pers.warren.ioc.annotation.Coke;
import pers.warren.ioc.annotation.Init;
import pers.warren.ioc.core.*;
import pers.warren.ioc.enums.BeanType;
import pers.warren.ioc.handler.CokePostHandler;
import pers.warren.ioc.handler.CokePostService;
import pers.warren.ioc.inject.Inject;
import pers.warren.ioc.loader.Loader;
import pers.warren.ioc.util.ReflectUtil;
import pers.warren.ioc.util.ScanUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

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

        beanDeduce();                //bean推断

        Inject.injectFiled();

        end();                       //容器启动后置方法

        return Container.getContainer().applicationContext();
    }

    private static void beanDeduce() {
        Container container = Container.getContainer();
        List<BeanDeduce> beanDeduces = container.getBeans(BeanDeduce.class);
        for (BeanDeduce beanDeduce : beanDeduces) {
            beanDeduce.deduce();
        }
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
        LinkedList<BeanDefinition> bdfDQueue = new LinkedList<>(container.getBeanDefinitions(BeanType.CONFIGURATION));
        bdfDQueue.addAll(container.getBeanDefinitions(BeanType.COMPONENT));
        bdfDQueue.addAll(container.getBeanDefinitions(BeanType.SIMPLE_BEAN));
        bdfDQueue.addAll(container.getBeanDefinitions(BeanType.PROXY));
        bdfDQueue.addAll(container.getBeanDefinitions(BeanType.OTHER));
        while (bdfDQueue.size() != 0) {
            BeanDefinition bdf = bdfDQueue.poll();
            createBean(bdf);
        }
    }

    private static void createBean(BeanDefinition beanDefinition) {
        Container container = Container.getContainer();
        if (null != container.getBean(beanDefinition.getName())) {
            return;
        }
        BeanFactory beanFactory = (BeanFactory) container.getBean(beanDefinition.getBeanFactoryClass());
        FactoryBean factoryBean = null;
        try {
            factoryBean = beanFactory.createBean(beanDefinition);
        } catch (Exception e) {
            e.printStackTrace();
        }
        container.addComponent(beanDefinition.getName(), factoryBean.getObject());
    }


    /**
     * 启动开始
     */
    private static void start() {
        addEliminator();
        startTimeMills = System.currentTimeMillis();
        printBanner();
    }

    private static void addEliminator() {
        Class<?> mainApplicationClass = ReflectUtil.deduceMainApplicationClass();
        if (null == mainApplicationClass) {
            log.error("推测的main方法未空!");
            return;
        }
        if (ReflectUtil.containsAnnotation(mainApplicationClass, Coke.class)) {
            Coke annotation = mainApplicationClass.getAnnotation(Coke.class);
            Container.getContainer().getEliminator().addExcludeBeanClzList(annotation.excludeClass());
            Container.getContainer().getEliminator().addExcludeBeanNameList(annotation.excludeBeanName());
        }
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
        initMethodRun();
        postHandlerRun();
        log.info("coke start ok! cost = {} ms !", System.currentTimeMillis() - startTimeMills);
        postServiceRun();
    }

    private static void initMethodRun() {
        Container container = Container.getContainer();
        Collection<BeanDefinition> beanDefinitions = container.getBeanDefinitions();
        for (BeanDefinition beanDefinition : beanDefinitions) {
            List<Method> methodList = Arrays.stream(beanDefinition.getClz().getDeclaredMethods())
                    .filter(method -> ReflectUtil.containsAnnotation(method, Init.class))
                    .collect(Collectors.toList());
            if (CollUtil.size(methodList) == 1 && methodList.get(0).getParameterTypes().length == 0) {
                Object bean = container.getBean(beanDefinition.getName());
                try {
                    methodList.get(0).invoke(bean, methodList.get(0));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static void postServiceRun() {
        Container container = Container.getContainer();
        List<CokePostService> postHandlers = container.getBeans(CokePostService.class);
        if (CollUtil.isNotEmpty(postHandlers)) {
            log.info("coke start cost {} ms before post handler run !", System.currentTimeMillis() - startTimeMills);
        }
        for (CokePostService postHandler : postHandlers) {
            try {
                postHandler.run();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
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
            try {
                postHandler.run();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }


}

