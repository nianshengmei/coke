package pers.warren.ioc.core;

import lombok.Data;
import lombok.ToString;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

@Data
@ToString
public class ValueField {

    private Field field;

    private String key;

    private String defaultValue;

    private Object configValue;

    private Class<?> type;

    private String sourceBeanName;

    private Type genericType;

}
