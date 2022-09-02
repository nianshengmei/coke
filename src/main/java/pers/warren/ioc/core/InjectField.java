package pers.warren.ioc.core;

import lombok.Data;

import java.lang.reflect.Field;

/**
 * 需要被COKE注入bean的字段
 */
@Data
public class InjectField {

    /**
     * 需要被注入的bean的名称
     */
    private String sourceBeanName;

    /**
     * 需要注入的字段
     */
    private Field field;

    /**
     * 是否注入代理对象,默认为false
     */
    private boolean proxy;

    public InjectField(String sourceBeanName, Field field, boolean proxy) {
        this.sourceBeanName = sourceBeanName;
        this.field = field;
        this.proxy = proxy;
    }

    public InjectField(String sourceBeanName, Field field) {
        this.sourceBeanName = sourceBeanName;
        this.field = field;
        this.proxy = false;
    }
}
