package pers.warren.ioc.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import pers.warren.ioc.core.BeanDefinition;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Signal {

    private String beanName;

    private BeanDefinition beanDefinition;

    /**
     * 注入bean的名称
     */
    private String sourceBeanName;

    private LifeCycleStep step;


    public Signal(BeanDefinition beanDefinition) {
        this.beanName = beanDefinition.getName();
        this.beanDefinition = beanDefinition;
    }
}
