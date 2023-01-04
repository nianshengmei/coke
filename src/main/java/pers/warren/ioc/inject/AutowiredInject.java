package pers.warren.ioc.inject;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import pers.warren.ioc.annotation.Autowired;
import pers.warren.ioc.core.BeanDefinition;
import pers.warren.ioc.core.Container;
import pers.warren.ioc.core.InjectField;
import pers.warren.ioc.ec.NoMatchBeanException;

import java.lang.reflect.Field;

/**
 * 基于autowired的注入
 *
 * @author warren
 */
@Slf4j
public class AutowiredInject implements Inject {

    @Override
    public void inject(BeanDefinition beanDefinition) {
        Object bean = Container.getContainer().getBean(beanDefinition.getName());
        if (CollUtil.isEmpty(beanDefinition.getAutowiredFieldInject())) {
            return;
        }
        for (InjectField injectField : beanDefinition.getAutowiredFieldInject()) {
            Field field = injectField.getField();
            String name = field.getName();
            Autowired annotation = field.getAnnotation(Autowired.class);
            Object b = null;
            if (StrUtil.isNotEmpty(annotation.value())) {
                name = annotation.value();
                b = getBean(name, injectField.isProxy());
                if (null == b) {
                    throw new RuntimeException("without bean autowired named :" + name
                            + "  , source bean" + beanDefinition.getName() + " ,Class name " + beanDefinition.getClz().getName()
                    );
                }

            } else {
                b = getBean(field.getType(), injectField.isProxy());
                if (null == b) {
                    throw new RuntimeException("no bean type autowired :" + field.getType().getName()
                            + "  , source bean " + beanDefinition.getName() + " ,Class name " + beanDefinition.getClz().getName()
                    );
                }
            }
            try {
                field.setAccessible(true);
                field.set(bean, b);
            } catch (Exception e) {
                if (null == bean) {
                    throw new RuntimeException("the bean " + beanDefinition.getName() + " not instantiation !");
                }
                throw new NoMatchBeanException("no bean type autowired :" + field.getType().getName()
                        + "  , source bean " + beanDefinition.getName() + " ,Class name " + beanDefinition.getClz().getName()
                );
            }
        }

    }

    @Override
    public Logger getLogger() {
        return log;
    }
}
