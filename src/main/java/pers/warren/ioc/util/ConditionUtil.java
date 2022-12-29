package pers.warren.ioc.util;


import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import pers.warren.ioc.condition.*;

import java.lang.reflect.Method;

public class ConditionUtil {

    public static int hasCondition(Method method) {
        if (method.getAnnotation(ConditionalOnBean.class) != null) {
            return 1;
        } else if (method.getAnnotation(ConditionalOnMissingBean.class) != null) {
            return 2;
        } else if (method.getAnnotation(ConditionalOnProperties.class) != null) {
            return 3;
        } else if (method.getAnnotation(ConditionalOnMissingProperties.class) != null) {
            return 4;
        }
        return 0;
    }


    public static boolean conditionMatch(int conditionModify, Method method, ConditionContext conditionContext) {
        switch (conditionModify) {
            case 1:
                ConditionalOnBean conditionalOnBeanAnnotation = method.getAnnotation(ConditionalOnBean.class);
                return handleConditionalOnBeanAnnotation(conditionalOnBeanAnnotation, conditionContext);

            case 2:
                ConditionalOnMissingBean conditionalOnMissingBeanAnnotation = method.getAnnotation(ConditionalOnMissingBean.class);
                return handleConditionalOnMissingBeanAnnotation(conditionalOnMissingBeanAnnotation, conditionContext);

            case 3:
                ConditionalOnProperties conditionalOnPropertiesAnnotation = method.getAnnotation(ConditionalOnProperties.class);
                return handleConditionalOnPropertiesAnnotation(conditionalOnPropertiesAnnotation, conditionContext);

            case 4:
                ConditionalOnMissingProperties conditionalOnMissingPropertiesAnnotation = method.getAnnotation(ConditionalOnMissingProperties.class);
                return handleConditionalOnMissingPropertiesAnnotation(conditionalOnMissingPropertiesAnnotation, conditionContext);

            default:
                return true;
        }
    }

    private static boolean handleConditionalOnMissingPropertiesAnnotation(ConditionalOnMissingProperties annotation, ConditionContext conditionContext) {
        ConditionalProperty[] conditionalProperties = annotation.value();
        for (ConditionalProperty property : conditionalProperties) {
            String key = property.key();
            String value = property.value();
            if (StrUtil.isEmpty(key)) {
                continue;
            }
            Object v = conditionContext.getEnvironment().getProperty(key);
            if (null == v) {
                continue;

            } else {
                if (StrUtil.isEmpty(value)) {
                    return false;
                }
                if (value.equals(v)) {
                    return false;
                }
            }

        }
        return true;
    }

    private static boolean handleConditionalOnPropertiesAnnotation(ConditionalOnProperties annotation, ConditionContext conditionContext) {
        ConditionalProperty[] conditionalProperties = annotation.value();
        for (ConditionalProperty property : conditionalProperties) {
            String key = property.key();
            String value = property.value();
            if (StrUtil.isEmpty(key)) {
                continue;
            }
            Object v = conditionContext.getEnvironment().getProperty(key);
            if (null == v) {
                return false;
            }
            if (!StrUtil.isEmpty(value)) {
                if (value.equals(v)) {
                    continue;
                }
                return false;
            }
        }
        return true;
    }

    private static boolean handleConditionalOnMissingBeanAnnotation(ConditionalOnMissingBean annotation, ConditionContext conditionContext) {
        String[] typeNames = annotation.typeName();
        Class<?>[] types = annotation.value();
        String[] names = annotation.name();

        for (Class<?> type : types) {
            if (conditionContext.getRegistry().containsBeanDefinition(type)) {
                return false;
            }
        }

        try {
            for (String typeName : typeNames) {
                Class<?> type = ClassUtil.loadClass(typeName);
                if (conditionContext.getRegistry().containsBeanDefinition(type)) {
                    return false;
                }
            }
        } catch (Throwable e) {
            return false;
        }

        for (String name : names) {
            if (conditionContext.getRegistry().containsBeanDefinition(name)) {
                return false;
            }
        }
        return true;
    }

    private static boolean handleConditionalOnBeanAnnotation(ConditionalOnBean annotation, ConditionContext conditionContext) {
        String[] typeNames = annotation.typeName();
        Class<?>[] types = annotation.value();
        String[] names = annotation.name();

        for (Class<?> type : types) {
            if (!conditionContext.getRegistry().containsBeanDefinition(type)) {
                return false;
            }
        }

        try {
            for (String typeName : typeNames) {
                Class<?> type = ClassUtil.loadClass(typeName);
                if (!conditionContext.getRegistry().containsBeanDefinition(type)) {
                    return false;
                }
            }
        } catch (Throwable e) {
            return false;
        }


        for (String name : names) {
            if (!conditionContext.getRegistry().containsBeanDefinition(name)) {
                return false;
            }
        }
        return true;
    }
}
