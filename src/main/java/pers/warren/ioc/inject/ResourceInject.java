package pers.warren.ioc.inject;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import pers.warren.ioc.core.BeanDefinition;
import pers.warren.ioc.event.Signal;

import javax.annotation.Resource;
import java.lang.reflect.Field;

/**
 * javax.annotation.Resource的注入支持
 *
 * @author warren
 */
@Slf4j
public class ResourceInject implements Inject {
    @Override
    public void inject(BeanDefinition beanDefinition) {
        if(beanDefinition.isProxy()){
            return;
        }
        Object bean = container.getBean(beanDefinition.getName());
        if (CollUtil.isEmpty(beanDefinition.getResourceFieldInject()) || beanDefinition.isProxy()) {
            return;
        }
        for (InjectField injectField : beanDefinition.getResourceFieldInject()) {
            Field field = injectField.getField();
            String name = field.getName();
            Resource annotation = field.getAnnotation(Resource.class);
            BeanDefinition b = null;
            if (StrUtil.isNotEmpty(annotation.name())) {
                name = annotation.name();
                b = container.getBeanDefinition(name);
                if (null == b) {
                    throw new RuntimeException("without bean autowired named :" + name
                            + "  , source bean " + beanDefinition.getName() + " ,Class name " + beanDefinition.getClz().getName()
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
                container.runEvent(new Signal(beanDefinition).setFieldBeanName(b.getName()), beanDefinition.getWhenFieldInjectEvent());
            } catch (Exception e) {
                throw new RuntimeException("no bean type autowired :" + field.getType().getName()
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
