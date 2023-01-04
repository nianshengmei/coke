package pers.warren.ioc.inject;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import pers.warren.ioc.core.BeanDefinition;
import pers.warren.ioc.core.InjectField;

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
        Object bean = container.getBean(beanDefinition.getName());
        if (CollUtil.isEmpty(beanDefinition.getResourceFieldInject()) || beanDefinition.isProxy()) {
            return;
        }
        for (InjectField injectField : beanDefinition.getResourceFieldInject()) {
            Field field = injectField.getField();
            String name = field.getName();
            Resource annotation = field.getAnnotation(Resource.class);
            Object b = null;
            if (StrUtil.isNotEmpty(annotation.name())) {
                name = annotation.name();
                b = getBean(name,injectField.isProxy());
                if (null == b) {
                    throw new RuntimeException("without bean autowired named :" + name
                            + "  , source bean " + beanDefinition.getName() + " ,Class name " + beanDefinition.getClz().getName()
                    );
                }

            } else {
                b = getBean(field.getType(),injectField.isProxy());
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
