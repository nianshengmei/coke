package pers.warren.ioc.core;

import lombok.Data;

import java.lang.reflect.Field;

@Data
public class ValueField {

    private Field field;

    private String key;

    private String defaultValue;

    private Object configValue;

    private Class<?> type;

    private String sourceBeanName;

}
