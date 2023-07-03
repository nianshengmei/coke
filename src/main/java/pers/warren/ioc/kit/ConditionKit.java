package pers.warren.ioc.kit;


import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import pers.warren.ioc.condition.*;
import pers.warren.ioc.core.BeanDefinitionRegistry;
import pers.warren.ioc.core.Container;

import java.lang.reflect.Method;

/**
 * condition工具类
 *
 * @author warren
 *
 * @since 1.0.3
 */
public class ConditionKit {

    /**
     * 判断方法是不是有condition注解
     *
     * @since 1.0。3
     */
    public static int hasCondition(Method method) {
        if (method.getAnnotation(ConditionalOnBean.class) != null) {
            return 1;
        } else if (method.getAnnotation(ConditionalOnMissingBean.class) != null) {
            return 2;
        } else if (method.getAnnotation(ConditionalOnProperties.class) != null) {
            return 3;
        } else if (method.getAnnotation(ConditionalOnMissingProperties.class) != null) {
            return 4;
        } else if (method.getAnnotation(ConditionalOnWebEnvironment.class) != null) {
            return 5;
        } else if (method.getAnnotation(ConditionalOnNotWebEnvironment.class) != null) {
            return 6;
        } else if (method.getAnnotation(ConditionalOnBeanCountLessThan.class) != null) {
            return 7;
        } else if (method.getAnnotation(ConditionalOnBeanCountMoreThan.class) != null) {
            return 8;
        }
        return 0;
    }

    /**
     * 条件匹配
     */
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
            case 5:
                return handleConditionalOnWebEnvironment(conditionContext);
            case 6:
                return handleConditionalOnNotWebEnvironment(conditionContext);
            case 7:
                ConditionalOnBeanCountLessThan conditionalOnBeanCountLessThanAnnotation = method.getAnnotation(ConditionalOnBeanCountLessThan.class);
                return handleConditionalOnBeanCountLessThan(conditionalOnBeanCountLessThanAnnotation, conditionContext);
            case 8:
                ConditionalOnBeanCountMoreThan conditionalOnBeanCountMoreThanAnnotation = method.getAnnotation(ConditionalOnBeanCountMoreThan.class);
                return handleConditionalOnBeanCountMoreThan(conditionalOnBeanCountMoreThanAnnotation, conditionContext);
            default:
                return true;
        }
    }

    /**
     * 处理bean小于指定数量的condition注解
     *
     * @since 1.0.1
     */
    private static boolean handleConditionalOnBeanCountLessThan(ConditionalOnBeanCountLessThan annotation, ConditionContext conditionContext) {
        BeanDefinitionRegistry registry = conditionContext.getRegistry();
        Class<?>[] values = annotation.value();
        if (values.length < 1) {
            return true;
        }
        int beanCount = registry.getBeanCount(values[0]);
        return beanCount < annotation.count();
    }

    /**
     * 处理bean大于指定数量的condition注解
     *
     * @since 1.0.1
     */
    private static boolean handleConditionalOnBeanCountMoreThan(ConditionalOnBeanCountMoreThan annotation, ConditionContext conditionContext) {
        BeanDefinitionRegistry registry = conditionContext.getRegistry();
        Class<?>[] values = annotation.value();
        if (values.length < 1) {
            return true;
        }
        int beanCount = registry.getBeanCount(values[0]);
        return beanCount > annotation.count();
    }
    
    /**
     * 处理非web环境的判断
     * 
     * @since 1.0.1
     */
    private static boolean handleConditionalOnNotWebEnvironment(ConditionContext conditionContext) {
        return !handleConditionalOnWebEnvironment(conditionContext);
    }
    
    /**
     * 处理web环境的判断
     * 
     * @since 1.0.1
     */
    private static boolean handleConditionalOnWebEnvironment(ConditionContext conditionContext) {
        return Container.getContainer().isWebEnvironment();
    }

    /**
     * 处理存在指定配置文件的condition注解
     *
     * @since 1.0.1
     */
    private static boolean handleConditionalOnMissingPropertiesAnnotation(ConditionalOnMissingProperties annotation, ConditionContext conditionContext) {
        ConditionalProperty[] conditionalProperties = annotation.value();
        for (ConditionalProperty property : conditionalProperties) {
            String key = property.key();
            String value = property.value();
            if (StrUtil.isEmpty(key)) {
                continue;
            }
            Object v = conditionContext.getEnvironment().getProperty(key);
            if (null != v) {
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
    
    /**
     * 处理存在指定配置文件的condition注解
     * 
     * @since 1.0.1
     */
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

    /**
     * 处理不存在指定bean的注解
     *
     * @since 1.0.1
     */
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

    /**
     * 处理存在指定bean的condition注解
     * 
     * @since 1.0.1
     */
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
