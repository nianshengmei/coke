package pers.warren.ioc.core;

import lombok.Data;
import pers.warren.ioc.enums.BeanType;
import pers.warren.ioc.event.Event;
import pers.warren.ioc.inject.InjectField;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class BeanDefinition {

    /**
     * bean名称
     */
    protected String name;

    /**
     * bean的类型
     */
    protected Class<?> clz;

    /**
     * 是否单例
     */
    protected boolean singleton;

    /**
     * 是否代理
     */
    private boolean proxy;

    /**
     * 因为什么注解被扫描为BeanDefinition
     */
    protected Class<?> scanByAnnotationClass;

    /**
     * 因为什么注解被扫描为BeanDefinition
     */
    protected Annotation scanByAnnotation;

    /**
     * bean类型
     */
    protected BeanType beanType;

    /**
     * 创建Bean方法
     */
    protected Object invokeFunction;

    /**
     * 用于@Bean生成的bean源
     */
    protected String invokeSource;

    /**
     * 增强扩展预留
     */
    protected List<PropertyValue> propertyValues;

    /**
     * 标注了@Autowired的字段
     */
    protected List<InjectField> autowiredFieldInject;

    /**
     * 标注了@Resource的字段
     */
    protected List<InjectField> resourceFieldInject;

    /**
     * 标注了@Value的字段
     */
    protected List<InjectField> valueFiledInject;

    /**
     * 用于创建的beanFactory类型
     */
    protected Class<?>  beanFactoryClass;

    /**
     * 使用那个factoryBean去getObject
     */
    protected Class<?> factoryBeanClass;

    protected BeanDefinition() {
        this.singleton = true;
    }

    /**
     * bean注册器
     */
    protected BeanRegister register;

    /**
     * 扩展字段
     */
    protected Map<String,BeanDefinitionExtendedField> extendedFields;

    protected AnnotationMetadata annotationMetadata;

    /**
     * 注册beanDefinition事件
     */
    protected List<Class<? extends Event>> registerEvent;

    /**
     * 前置处理事件
     */
    protected  List<Class<? extends Event>> beforeProcessorEvent;

    /**
     * 后置处理事件
     */
    protected  List<Class<? extends Event>> afterProcessorEvent;

    protected  List<Class<? extends Event>> afterInitializationEvent;

    /**
     * 字段注入事件
     */
    protected  List<Class<? extends Event>> whenFieldInjectEvent;

    /**
     * bean的初始化优先级
     *
     * <p>默认为 0</p>
     * <p>优先级 0 - 9 </p>
     */
    protected int priority;

    /**
     * 初始化落后于
     */
    protected List<String> loadAfter = new ArrayList<>();

    protected boolean load;

    /**
     * 是否懒加载
     */
    protected boolean lazy;

    protected int step = 0;
}
