package pers.warren.ioc.inject;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import pers.warren.ioc.annotation.Autowired;
import pers.warren.ioc.core.BeanDefinition;
import pers.warren.ioc.core.Container;
import pers.warren.ioc.ec.NoMatchBeanException;
import pers.warren.ioc.event.LifeCycleSignal;

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
        if(beanDefinition.isProxy()){
            return;
        }
        Object bean = Container.getContainer().getBean(beanDefinition.getName());
        if (CollUtil.isEmpty(beanDefinition.getAutowiredFieldInject())) {
            return;
        }
        for (InjectField injectField : beanDefinition.getAutowiredFieldInject()) {
            Field field = injectField.getField();
            String name = field.getName();
            Autowired annotation = field.getAnnotation(Autowired.class);
            BeanDefinition b = null;
            if (null != annotation && StrUtil.isNotEmpty(annotation.value())) {
                name = annotation.value();
                b = container.getBeanDefinition(name);
                if (null == b) {
                    throw new RuntimeException("without bean autowired named :" + name
                            + "  , source bean" + beanDefinition.getName() + " ,Class name " + beanDefinition.getClz().getName()
                    );
                }

            } else {
                b = container.getBeanDefinition(field.getType());
                if (null == b) {
                    throw new RuntimeException("no bean type autowired :" + field.getType().getName()
                            + "  , source bean " + beanDefinition.getName() + " ,Class name " + beanDefinition.getClz().getName()
                    );
                }
            }
            try {
                field.setAccessible(true);
                Object filedBean = container.getBean(b.getName());
                field.set(bean, filedBean);
                container.runEvent(new LifeCycleSignal(beanDefinition).setFieldBeanName(b.getName()), beanDefinition.getWhenFieldInjectEvent());
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
