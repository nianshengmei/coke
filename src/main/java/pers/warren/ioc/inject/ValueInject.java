package pers.warren.ioc.inject;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import pers.warren.ioc.core.BeanDefinition;
import pers.warren.ioc.core.ValueField;
import pers.warren.ioc.util.InjectUtil;

import java.lang.reflect.Field;
import java.util.List;

@Slf4j
public class ValueInject implements Inject{
    @Override
    public void inject(BeanDefinition beanDefinition) {
        List<ValueField> valueFiledInject = beanDefinition.getValueFiledInject();
        for (ValueField field : valueFiledInject) {
            if (null == field.getConfigValue() && null == field.getDefaultValue()) {
                continue;
            }
            Field f = field.getField();
            Object bean = container.getBean(beanDefinition.getName());
            try {
                Object value = InjectUtil.getDstValue(field);
                f.setAccessible(true);
                f.set(bean, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException("the value in the configuration file cannot be converted to the corresponding attribute , value field info : " + field);
            }
        }
    }

    @Override
    public Logger getLogger() {
        return log;
    }
}
