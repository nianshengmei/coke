package pers.warren.ioc.core;

import lombok.Data;

/**
 * 用于扩展BeanDefinition专用
 */
@Data
public class BeanDefinitionExtendedField {

    private Class<?> fieldType;

    private Object value;
}
