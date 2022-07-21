package pers.warren.ioc;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import pers.warren.ioc.core.*;
import pers.warren.ioc.enums.BeanType;
import pers.warren.ioc.handler.CokePropertiesHandler;
import pers.warren.ioc.util.ScanUtil;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

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

    public void injectProperties() {
        Container container = Container.getContainer();
        List<BeanDefinition> beanDefinitions = container.getBeanDefinitions(BeanType.CONFIGURATION);
        for (BeanDefinition beanDefinition : beanDefinitions) {
            List<ValueField> valueFiledInject = beanDefinition.getValueFiledInject();
            for (ValueField field : valueFiledInject) {
                Field f = field.getField();
                Object bean = container.getBean(beanDefinition.getName());
                Object value = getDstValue(field);
                f.setAccessible(true);
                try {
                    f.set(bean, value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private Object getDstValue(ValueField field) {
        String vs = "";
        if (StrUtil.isNotEmpty((String) field.getConfigValue())) {
            vs = (String) field.getConfigValue();
        } else {
            vs = field.getDefaultValue();
        }
        if (field.getType().getTypeName().equals(Integer.class.getTypeName())) {
            return Integer.parseInt(vs));
        } else if (field.getType().getTypeName().equals(Double.class.getTypeName())) {
            field.set(component, Double.parseDouble(value));
        } else if (field.getType().getTypeName().equals(Long.class.getTypeName()) ||
                field.getType().getTypeName().equals(long.class.getTypeName())) {
            if("[]".equals(value))continue;
            field.set(component, Long.parseLong(value));
        } else if (field.getType().getTypeName().equals(Boolean.class.getTypeName())) {
            field.set(component, Boolean.parseBoolean(value));
        } else if (field.getType().getTypeName().equals(Short.class.getTypeName())) {
            field.set(component, Short.parseShort(value));
        } else if (field.getType().getTypeName().equals(Byte.class.getTypeName())) {
            field.set(component, Byte.parseByte(value));
        } else if (field.getType().getTypeName().equals(Character.class.getTypeName())) {
            field.set(component, value);
        } else if (field.getType().getTypeName().equals(String.class.getTypeName())) {
            field.set(component, value);
        } else if (field.getType().getTypeName().equals("int")) {
            ConfigBean finalConfigBean = configBean;
            new ExceptionRunner(() -> {
                field.set(component, Integer.parseInt(value));
                return true;
            }, (e) -> {
                log.debug("配置文件中未找到该配置: " + finalConfigBean.getBeanKey());
                return false;
            }).run();
        } else if (field.getType().getTypeName().equals("double")) {
            field.set(component, Double.parseDouble(value));
        } else if (field.getType().getTypeName().equals("boolean")) {
            field.set(component, Boolean.parseBoolean(value));
        } else if (field.getType().getTypeName().equals(ArrayList.class.getTypeName())) {
            valueInjectList(field, configBean, component);
        } else if (field.getType().getTypeName().equals(LinkedList.class.getTypeName())) {
            valueInjectList(field, configBean, component);
        } else if (field.getType().getTypeName().equals(List.class.getTypeName())) {
            valueInjectList(field, configBean, component);
        } else if (field.getType().getTypeName().equals(String[].class.getTypeName())) {
            String[] array = new String[((List) configBean.getBeanValue()).size()];
            field.set(component, ((List) configBean.getBeanValue()).toArray(array));
        } else if (field.getType().getTypeName().equals(Integer[].class.getTypeName())) {
            Integer[] array = new Integer[((List) configBean.getBeanValue()).size()];
            List list = (List) configBean.getBeanValue();
            for (int i = 0; i < array.length; i++) {
                array[i] = Integer.parseInt(list.get(i).toString());
            }
            field.set(component, array);
        } else if (field.getType().getTypeName().equals(Double[].class.getTypeName())) {
            Double[] array = new Double[((List) configBean.getBeanValue()).size()];
            List list = (List) configBean.getBeanValue();
            for (int i = 0; i < array.length; i++) {
                array[i] = Double.parseDouble(list.get(i).toString());
            }
            field.set(component, array);
        } else if (field.getType().getClass().isAssignableFrom(Date.class)) {
            long time = DateUtil.parse(value).getTime();
            Object o = field.getType().getConstructor(long.class).newInstance(time);
            field.set(component, o);
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

