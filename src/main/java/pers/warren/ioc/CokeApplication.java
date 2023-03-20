package pers.warren.ioc;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import pers.warren.ioc.annotation.*;
import pers.warren.ioc.core.*;
import pers.warren.ioc.enums.BeanType;
import pers.warren.ioc.event.LifeCycleStep;
import pers.warren.ioc.event.Signal;
import pers.warren.ioc.handler.CokePostHandler;
import pers.warren.ioc.handler.CokePostService;
import pers.warren.ioc.inject.Inject;
import pers.warren.ioc.loader.LoadPair;
import pers.warren.ioc.loader.Loader;
import pers.warren.ioc.pool.CokeThreadPool;
import pers.warren.ioc.util.ReflectUtil;
import pers.warren.ioc.util.ScanUtil;

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
        start(clz);      //IOC启动前置方法,包括打印banner

        scan(); //包扫描

        loadEnvironment();  //读取配置文件

        log.info("scan java and resource files ok, cost {} ms !", System.currentTimeMillis() - startTimeMills);

        preload();   //预加载

        initBeanDefinition();         //扫描需要初始化的Bean生成BeanDefinition

        componentPostProcessorBefore();  // BeanPostProcessor的前置方法

        loadBean();                  //初始化Bean , 在这一步会将bean生成后丢入容器，并调用BeanPostProcessor的后置方法，

        beanDeduce();                //bean推断(暂时未使用)

        inject();                    //注入

        end();                       //容器启动后置方法

        return Container.getContainer().applicationContext();
    }

    /**
     * 启动开始
     */
    private static void start(Class<?> clz) {
        ReflectUtil.customEntryClass = clz;
        addEliminator();
        startTimeMills = System.currentTimeMillis();
        printBanner();
    }

    /**
     * 包扫描
     */
    private static void scan() {
        clzSet = ScanUtil.scan();
    }

    /**
     * 加载配置文件等环境参数
     */
    private static void loadEnvironment() {
        Loader.loadConfigEnvironment();
    }

    /**
     * 预加载
     * <p>
     * 所谓加载就是bean的初始化,预加载的本质就是将一部分核心组件先加载成bean,
     * 比如ApplicationContext,BeanRegister,BeanFactory,BeanPostProcessor和开发者指定预加载项
     */
    private static void preload() {
        Loader.preload(clzSet);
    }


    /**
     * 该方法会执行BeanPostProcessor的前置方法
     */
    private static void componentPostProcessorBefore() {
        Container container = Container.getContainer();
        Collection<BeanDefinition> beanDefinitions = container.getBeanDefinitions();
        List<BeanPostProcessor> beanPostProcessors = container.getBeans(BeanPostProcessor.class);


        //参考生命周期 step 6 - BeanPostProcessor前置方法执行
        for (BeanDefinition beanDefinition : beanDefinitions) {
            if (0 == beanDefinition.getStep()) {   // 0代表此beandefinition未执行过BeanPostProcessor前置方法，防止重复执行
                for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
                    beanPostProcessor.postProcessBeforeInitialization(beanDefinition, beanDefinition.getRegister());
                }
                container.runEvent(new Signal(beanDefinition).setStep(LifeCycleStep.BEFORE_PROCESSOR), beanDefinition.getBeforeProcessorEvent());
                beanDefinition.setStep(1);
            }
        }
        //重新获取beanDefinition,因为beanPostProcessor.postProcessBeforeInitialization中初始化了@Bean定义的bean的BeanDefinition
        beanDefinitions = container.getBeanDefinitions();

        //参考生命周期 step 7 - BeanPostProcessor前置方法的后置方法执行
        for (BeanDefinition beanDefinition : beanDefinitions) {
            if (1 == beanDefinition.getStep()) {  //1代表此beandefinition未执行过BeanPostProcessor前置方法的后置方法执行，防止重复执行

                for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
                    beanPostProcessor.postProcessAfterBeforeProcessor(beanDefinition, beanDefinition.getRegister());
                }
                container.runEvent(new Signal(beanDefinition).setStep(LifeCycleStep.AFTER_PROCESSOR), beanDefinition.getAfterProcessorEvent());
                beanDefinition.setStep(2);
            }
        }

        beanDefinitions.stream().forEach(beanDefinition -> {
            //beanRegister注册BeanDefinition
            //设置优先级
            if (beanDefinition != null && null != beanDefinition.getInvokeFunction()) {
                Method method = (Method) beanDefinition.getInvokeFunction();
                Priority priority = method.getAnnotation(Priority.class);
                if(null != priority){
                    beanDefinition.setPriority(priority.priority());
                }
                After after = method.getAnnotation(After.class);
                if(null != after){
                    beanDefinition.getLoadAfter().addAll(Arrays.asList(after.name()));
                }

                Before before = method.getAnnotation(Before.class);
                if(null != before){
                    String[] names = before.name();
                    for (String name : names) {
                        BeanDefinition bd = container.getBeanDefinition(name);
                        bd.getLoadAfter().add(beanDefinition.getName());
                    }
                }

                if (null != method.getAnnotation(Lazy.class)) {
                    beanDefinition.setLazy(true);
                }
            } else {
                Class<?> clz = beanDefinition.getClz();
                Priority priority = clz.getAnnotation(Priority.class);
                if(null != priority){
                    beanDefinition.setPriority(priority.priority());
                }

                After after = clz.getAnnotation(After.class);
                if(null != after){
                    beanDefinition.getLoadAfter().addAll(Arrays.asList(after.name()));
                }

                Before before = clz.getAnnotation(Before.class);
                if(null != before){
                    String[] names = before.name();
                    for (String name : names) {
                        BeanDefinition bd = container.getBeanDefinition(name);
                        bd.getLoadAfter().add(beanDefinition.getName());
                    }
                }

                if (null != clz.getAnnotation(Lazy.class)) {
                    beanDefinition.setLazy(true);
                }
            }
        });
    }

    private static void loadBean() {
        Container container = Container.getContainer();
        List<BeanPostProcessor> postProcessors = Container.getContainer().getBeans(BeanPostProcessor.class);

        //按照顺序添加到队列中,为了按顺序初始化
        LinkedList<BeanDefinition> bdfDQueue = new LinkedList<>(container.getBeanDefinitions(BeanType.CONFIGURATION));
        bdfDQueue.addAll(container.getBeanDefinitions(BeanType.COMPONENT));
        bdfDQueue.addAll(container.getBeanDefinitions(BeanType.SIMPLE_BEAN));
        bdfDQueue.addAll(container.getBeanDefinitions(BeanType.PROXY));
        bdfDQueue.addAll(container.getBeanDefinitions(BeanType.OTHER));
        bdfDQueue.sort((o1, o2) -> -NumberUtil.compare(o1.getPriority(), o2.getPriority()));

        while (bdfDQueue.size() != 0) {
            BeanDefinition bdf = bdfDQueue.poll();  //按顺序取出BeanDefinition
            if (bdf.isLoad() || loadAfter(bdf, bdfDQueue)) {
                continue;
            }
            createBean(bdf);  //创建bean并放入容器
            bdf.setLoad(true);
            if (2 == bdf.getStep()) {
                for (BeanPostProcessor postProcessor : postProcessors) {
                    postProcessor.postProcessAfterInitialization(bdf, bdf.getRegister());   //执行后置处理器
                }
                container.runEvent(new Signal(bdf).setStep(LifeCycleStep.AFTER_INITIALIZATION), bdf.getAfterInitializationEvent());
                bdf.setStep(3);   //已实例化
            }
        }
    }

    private static boolean loadAfter(BeanDefinition bd, LinkedList<BeanDefinition> bdfDQueue) {
        Container container = Container.getContainer();
        List<String> loadAfter = bd.getLoadAfter();
        if (loadAfter.size() > 0) {
            for (String name : loadAfter) {
                if ((null == container.getBean(name))) {
                    LoadPair pair = new LoadPair().setA(bd.getName()).setB(name);
                    if (container.containsPair(pair)) {
                        throw new RuntimeException("循环优先引用 "+ bd.getName() + " <=> "+name);
                    }
                    bdfDQueue.addFirst(bd);
                    bdfDQueue.addFirst(container.getBeanDefinition(name));
                    container.getPairs().add(pair);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 执行bean推断,暂时coke没有提供bean推断的具体实现,不推荐作为生命周期使用
     */
    private static void beanDeduce() {
        Container container = Container.getContainer();
        List<BeanDeduce> beanDeduces = container.getBeans(BeanDeduce.class);
        for (BeanDeduce beanDeduce : beanDeduces) {
            beanDeduce.deduce();
        }
    }

    /**
     * 注入操作(配置文件注入和bean注入)
     */
    private static void inject() {
        Inject.injectFiled();
    }

    /**
     * 创建bean
     */
    private static void createBean(BeanDefinition beanDefinition) {
        Container container = Container.getContainer();
        if (null != container.getBean(beanDefinition.getName())) {
            return;
        }
        BeanFactory beanFactory = (BeanFactory) container.getBean(beanDefinition.getBeanFactoryClass());
        if(beanDefinition.isLazy()){
            beanFactory = container.getBean(LazyBeanFactory.class);
        }
        FactoryBean factoryBean = null;
        try {
            factoryBean = beanFactory.createBean(beanDefinition);
        } catch (Exception e) {
            throw new RuntimeException("创建bean失败 "+ beanDefinition.getName());
        }
        container.addComponent(beanDefinition.getName(), factoryBean.getObject());
    }

    /**
     * 排除器
     */
    private static void addEliminator() {
        Class<?> mainApplicationClass = ReflectUtil.deduceMainApplicationClass();
        if (null == mainApplicationClass) {
            log.error("推测的main方法为空!");
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

    /**
     * 运行bean的类中加入了@Init注解的方法
     *
     * <p>注意一个类只能有一个@Init注解,且标注的方法不能有任何参数,否则将不作任何处理</p>
     */
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
                    methodList.get(0).invoke(bean);
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
        CokeThreadPool pool = container.getBean(CokeThreadPool.class);
        for (CokePostService postHandler : postHandlers) {
            pool.newTask(() -> {
                try {
                    postHandler.run();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            });
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

