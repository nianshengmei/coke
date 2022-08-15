package pers.warren.ioc.core;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import pers.warren.ioc.annotation.Autowired;
import pers.warren.ioc.annotation.Bean;
import pers.warren.ioc.annotation.Value;
import pers.warren.ioc.enums.BeanType;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DefaultBeanPostProcessor implements BeanPostProcessor {

    @Override
    public void postProcessBeforeInitialization(BeanDefinition beanDefinition, BeanRegister register) {
        Class<?> clz = beanDefinition.getClz();
        Field[] declaredFields = clz.getDeclaredFields();
        List<Field> autowiredFields = new ArrayList<>();
        List<Field> resourceFields = new ArrayList<>();
        List<ValueField> valueFields = new ArrayList<>();
        for (Field declaredField : declaredFields) {
            if (null != declaredField.getAnnotation(Autowired.class)) {
                autowiredFields.add(declaredField);
            }

            if (null != declaredField.getAnnotation(Resource.class)) {
                resourceFields.add(declaredField);
            }

            if (null != declaredField.getAnnotation(Value.class)) {
                Value valueAnnotation = declaredField.getAnnotation(Value.class);
                String value = valueAnnotation.value();
                ValueField field = new ValueField();
                field.setSourceBeanName(beanDefinition.getName());
                field.setField(declaredField);
                if (value.contains(":")) {
                    String[] vs = value.split(":");
                    field.setKey(vs[0]);
                    field.setDefaultValue(vs[1]);
                } else {
                    field.setKey(value);
                }
                Type genericType = declaredField.getGenericType();
                field.setGenericType(genericType);
                field.setType(declaredField.getType());
                ApplicationContext applicationContext = Container.getContainer().getBean(ApplicationContext.class.getSimpleName());
                field.setConfigValue(applicationContext.getProperty(field.getKey()));
                valueFields.add(field);
            }
        }
        beanDefinition.setAutowiredFieldInject(autowiredFields);
        beanDefinition.setResourceFieldInject(resourceFields);
        beanDefinition.setValueFiledInject(valueFields);

        Method[] methods = clz.getMethods();
        for (Method method : methods) {
            if (method.getAnnotation(Bean.class) != null) {
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
                    register.registerBeanDefinition(builder, Container.getContainer());
                }
            }
        }
        BeanPostProcessor.super.postProcessBeforeInitialization(beanDefinition, register);
    }

    @Override
    public void postProcessAfterInitialization(BeanDefinition beanDefinition, BeanRegister register) {
    }


}
