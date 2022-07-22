package pers.warren.ioc;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import lombok.extern.slf4j.Slf4j;
import pers.warren.ioc.core.*;
import pers.warren.ioc.enums.BeanType;
import pers.warren.ioc.handler.CokePropertiesHandler;
import pers.warren.ioc.util.InjectUtil;
import pers.warren.ioc.util.ScanUtil;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * 容器的启动辅助类
 */

@Slf4j
public class IocApplication {

    /**
     * 所有符合扫描规则下的类的集合
     */
    private static Set<Class<?>> clzSet;

    /**
     * 启动开始时间 -用于计算容器启动耗时
     */
    private static long startTimeMills;

    public static void run(Class<?> clz, String[] args) {
        start();
        clzSet = ScanUtil.scan();   //扫描类

        CokePropertiesHandler.read();

        loadConfiguration();


        loadBean();

        injectProperties();
        end();
    }

    private static void loadBean() {
        Container container = Container.getContainer();
        List<BeanDefinition> beanDefinitions = container.getBeanDefinitions(BeanType.CONFIGURATION);

        for (BeanDefinition beanDefinition : beanDefinitions) {
            BeanFactory beanFactory = (BeanFactory) container.getBean(beanDefinition.getBeanFactoryClass());
            FactoryBean factoryBean = beanFactory.createBean(beanDefinition);
            container.addFactoryBean(beanDefinition.getName(), factoryBean);
            container.addComponent(beanDefinition.getName(), factoryBean.getObject());
        }

        beanDefinitions = container.getBeanDefinitions(BeanType.COMPONENT);
        for (BeanDefinition beanDefinition : beanDefinitions) {
            BeanFactory beanFactory = (BeanFactory) container.getBean(beanDefinition.getBeanFactoryClass());
            FactoryBean factoryBean = beanFactory.createBean(beanDefinition);
            container.addFactoryBean(beanDefinition.getName(), factoryBean);
            container.addComponent(beanDefinition.getName(), factoryBean.getObject());
        }

        beanDefinitions = container.getBeanDefinitions(BeanType.SIMPLE_BEAN);
        for (BeanDefinition beanDefinition : beanDefinitions) {
            BeanFactory beanFactory = (BeanFactory) container.getBean(beanDefinition.getBeanFactoryClass());
            FactoryBean factoryBean = beanFactory.createBean(beanDefinition);
            container.addFactoryBean(beanDefinition.getName(), factoryBean);
            container.addComponent(beanDefinition.getName(), factoryBean.getObject());
        }

    }

    public static void injectProperties() {
        Container container = Container.getContainer();
        List<BeanDefinition> beanDefinitions = container.getBeanDefinitions(BeanType.CONFIGURATION);
        for (BeanDefinition beanDefinition : beanDefinitions) {
            List<ValueField> valueFiledInject = beanDefinition.getValueFiledInject();
            for (ValueField field : valueFiledInject) {
                Field f = field.getField();
                Object bean = container.getBean(beanDefinition.getName());

                try {
                    Object value = InjectUtil.getDstValue(field);
                    f.setAccessible(true);
                    f.set(bean, value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (Exception e){
                    throw new RuntimeException("the value in the configuration file cannot be converted to the corresponding attribute , value field info : "+field );
                }
            }
        }
    }

    public void injectField() {

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
        log.info("needcoke-ioc start ok! cost = {}ms", System.currentTimeMillis() - startTimeMills);
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
}

