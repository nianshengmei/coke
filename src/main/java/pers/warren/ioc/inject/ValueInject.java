package pers.warren.ioc.inject;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import pers.warren.ioc.core.BeanDefinition;
import pers.warren.ioc.event.DefaultEventBus;
import pers.warren.ioc.event.LifeCycleSignal;
import pers.warren.ioc.event.LifeCycleStep;
import pers.warren.ioc.event.Signal;
import pers.warren.ioc.util.InjectUtil;
import java.lang.reflect.Field;
import java.util.List;

@Slf4j
public class ValueInject implements Inject {
    @Override
    public void inject(BeanDefinition beanDefinition) {
        if(beanDefinition.isProxy()){
            return;
        }
        List<InjectField> valueFiledInject = beanDefinition.getValueFiledInject();
        if (CollUtil.isEmpty(valueFiledInject)) {
            return;
        }
        for (InjectField field : valueFiledInject) {
            if (null == field.getConfigValue() && null == field.getDefaultValue()) {
                continue;
            }
            Field f = field.getField();
            Object bean = container.getBean(beanDefinition.getName());
            try {
                Object value = InjectUtil.getDstValue(field);
                f.setAccessible(true);
                f.set(bean, value);
                new DefaultEventBus().sendSignal(new LifeCycleSignal(beanDefinition).setValueField(field).setStep(LifeCycleStep.BEAN_INJECT));
                container.runEvent(new LifeCycleSignal(beanDefinition).setValueField(field), beanDefinition.getWhenFieldInjectEvent());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                if(beanDefinition.getName().endsWith("#proxy")){
                    log.error("the value in the configuration file cannot be converted to the corresponding attribute , value field info : {}",field);
                    continue;
                }
                throw new RuntimeException("the value in the configuration file cannot be converted to the corresponding attribute , value field info : " + field,e);
            }
        }
    }

    @Override
    public Logger getLogger() {
        return log;
    }
}
