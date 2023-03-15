package pers.warren.ioc.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import pers.warren.ioc.core.BeanDefinition;
import pers.warren.ioc.core.ValueField;

@Data
@NoArgsConstructor
@ToString
@Accessors(chain = true)
public class Signal {

    private String beanName;

    private BeanDefinition beanDefinition;

    /**
     * 注入bean的名称
     */
    private String fieldBeanName;

    private ValueField valueField;

    private LifeCycleStep step;


    public Signal(BeanDefinition beanDefinition) {
        this.beanName = beanDefinition.getName();
        this.beanDefinition = beanDefinition;
    }
}
