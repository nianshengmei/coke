package pers.warren.ioc.inject;

import lombok.Data;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * 注入字段
 *
 * @author warren
 * @since v1.0.2
 */
@Data
public class InjectField {

    /**
     * 哪个bean需要被注入
     */
    private String sourceBeanName;

    /**
     * 需要注入的字段
     */
    private Field field;

    private Type fieldType;

    private InjectType type;

    /**
     * 配置文件的key
     */
    private String configKey;

    /**
     * 如果配置文件中没有读到，使用的默认值
     */
    private String defaultValue;

    /**
     * 配置文件中的值
     */
    private Object configValue;

    /**
     * 如果待注入字段使用了泛型，泛型的类型
     */
    private Type genericType;

    public InjectField(String sourceBeanName, Field field,InjectType type) {
        this.sourceBeanName = sourceBeanName;
        this.field = field;
        this.type = type;
    }

    public InjectField() {
    }
}
