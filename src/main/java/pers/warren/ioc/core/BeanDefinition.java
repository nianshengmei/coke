package pers.warren.ioc.core;

import lombok.Data;
import pers.warren.ioc.enums.BeanType;

import java.lang.reflect.Field;
import java.util.List;

@Data
public class BeanDefinition {

    /**
     * bean名称
     */
    private String name;

    /**
     * bean的类型
     */
    private Class<?> clz;

    /**
     * 是否单例
     */
    private boolean singleton;

    /**
     * bean类型
     */
    private BeanType beanType;

    /**
     * 创建Bean方法
     */
    private Object invokeFunction;

    /**
     * 用于@Bean生成的bean源
     */
    private String invokeSource;

    /**
     * 增强扩展预留
     */
    private List<PropertyValue> propertyValues;

    /**
     * 标注了@Autowired的字段
     */
    private List<Field> autowiredFieldInject;

    /**
     * 标注了@Resource的字段
     */
    private List<Field> resourceFieldInject;

    /**
     * 标注了@Value的字段
     */
    private List<ValueField> valueFiledInject;

    /**
     * 用于创建的beanFactory类型
     */
    private Class<?>  beanFactoryClass;

    /**
     * 使用那个factoryBean去getObject
     */
    private Class<?> factoryBeanClass;

    public BeanDefinition() {
        this.singleton = true;
    }

    /**
     * bean注册器
     */
    private BeanRegister register;
}
