package pers.warren.ioc.core;

import lombok.Data;
import lombok.ToString;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * 待注入的被@Value修饰的字段
 * @author warren
 * @since jdk8
 */
@Data
@ToString
public class ValueField {

    /**
     * 待注入字段
     */
    private Field field;

    /**
     * 配置文件key
     */
    private String key;

    /**
     * 如果配置文件中没有读到，使用的默认值
     */
    private String defaultValue;

    /**
     * 配置文件中的值
     */
    private Object configValue;

    /**
     * 待注入字段的类型
     */
    private Class<?> type;

    /**
     * 待注入字段所在Bean的名称
     */
    private String sourceBeanName;

    /**
     * 如果待注入字段使用了泛型，泛型的类型
     */
    private Type genericType;

}
