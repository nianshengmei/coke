package pers.warren.ioc.core;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import pers.warren.ioc.annotation.Autowired;
import pers.warren.ioc.annotation.Bean;
import pers.warren.ioc.annotation.Value;
import pers.warren.ioc.inject.InjectField;
import pers.warren.ioc.condition.ConditionContext;
import pers.warren.ioc.condition.DefaultConditionContext;
import pers.warren.ioc.enums.BeanType;
import pers.warren.ioc.inject.InjectType;
import pers.warren.ioc.kit.ConditionKit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 默认的bean的后置处理器
 *
 * @author warren
 * @since 1.0.3
 */
public class DefaultBeanPostProcessor implements BeanPostProcessor {

    /**
     * bean初始化前置处理
     *
     * <p>扫描bean需要被注入bean、或者配置的字段</p>
     *
     * @param beanDefinition bean定义
     * @param register       bean注册器
     * @since 1.0.3
     */
    @Override
    public void postProcessBeforeInitialization(BeanDefinition beanDefinition, BeanRegister register) {
        Class<?> clz = beanDefinition.getClz();
        Field[] declaredFields = ReflectUtil.getFields(clz);
        List<InjectField> autowiredFields = new ArrayList<>();
        List<InjectField> resourceFields = new ArrayList<>();
        List<InjectField> valueFields = new ArrayList<>();
        for (Field declaredField : declaredFields) {
            if (null != declaredField.getAnnotation(Autowired.class)) {
                autowiredFields.add(new InjectField(beanDefinition.name, declaredField, InjectType.BEAN));
            }

            if (null != declaredField.getAnnotation(Resource.class)) {
                resourceFields.add(new InjectField(beanDefinition.name, declaredField, InjectType.BEAN));
            }

            if (null != declaredField.getAnnotation(Value.class)) {
                Value valueAnnotation = declaredField.getAnnotation(Value.class);
                String value = valueAnnotation.value();
                InjectField field = new InjectField();
                field.setType(InjectType.CONFIG);
                field.setSourceBeanName(beanDefinition.getName());
                field.setField(declaredField);
                if (value.contains(":")) {
                    String[] vs = value.split(":");
                    field.setConfigKey(vs[0]);
                    field.setDefaultValue(vs[1]);
                } else {
                    field.setConfigKey(value);
                }
                Type genericType = declaredField.getGenericType();
                field.setGenericType(genericType);
                field.setFieldType(declaredField.getType());
                ApplicationContext applicationContext = Container.getContainer().getBean(ApplicationContext.class.getSimpleName());
                field.setConfigValue(applicationContext.getProperty(field.getConfigKey()));
                valueFields.add(field);
            }
        }
        beanDefinition.setAutowiredFieldInject(autowiredFields);
        beanDefinition.setResourceFieldInject(resourceFields);
        beanDefinition.setValueFiledInject(valueFields);

        Method[] methods = clz.getMethods();
        for (Method method : methods) {
            if (method.getAnnotation(Bean.class) != null) {
                int conditionModify = ConditionKit.hasCondition(method);
                ConditionContext conditionContext = new DefaultConditionContext();
                if (ConditionKit.conditionMatch(conditionModify, method, conditionContext)) {
                    Bean beanAnnotation = method.getAnnotation(Bean.class);
                    String name = beanAnnotation.name();
                    if (StrUtil.isNotEmpty(name)) {
                        if (Container.getContainer().isBeanNameInUse(name)) {
                            throw new RuntimeException("bean name in used :" + name);
                        }
                    } else {
                        name = method.getName();
                        if (Container.getContainer().isBeanNameInUse(name)) {
                            name += ("#" + RandomUtil.randomString(7));
                        }
                    }
                    if (!method.getReturnType().equals(Void.class)) {
                        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(method.getReturnType(),
                                name, BeanType.SIMPLE_BEAN, method, beanDefinition.getName()).setFactoryBeanType(SimpleFactoryBean.class);
                        builder.setRegister(register);
                        register.registerBeanDefinition(builder.build(), Container.getContainer());
                    }
                }
            }
        }
    }

    /**
     * bean初始化后置处理
     *
     * <p>处理bean的@PostConstruct注解</p>
     *
     * @param beanDefinition bean定义
     * @param register       bean注册器
     * @since 1.0.3
     */
    @Override
    public void postProcessAfterInitialization(BeanDefinition beanDefinition, BeanRegister register) {
        Class<?> clz = beanDefinition.getClz();
        Method[] declaredMethods = clz.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            PostConstruct annotation = declaredMethod.getAnnotation(PostConstruct.class);
            if (null != annotation) {
                Object bean = Container.getContainer().getBean(beanDefinition.getName());
                Parameter[] parameters = declaredMethod.getParameters();
                Object[] paramArr = null;
                if (null != parameters && parameters.length > 0) {
                    //TODO 容器中寻找一波参数
                } else {
                    paramArr = new Object[0];
                }
                try {
                    declaredMethod.invoke(bean, paramArr);
                } catch (Exception e) {
                    throw new RuntimeException("@PostConstruct error see class " + clz.getName() + " method = " + declaredMethod.getName(), e);
                }
            }
        }
    }


}
