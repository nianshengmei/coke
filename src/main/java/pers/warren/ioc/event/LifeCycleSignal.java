package pers.warren.ioc.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import pers.warren.ioc.core.BeanDefinition;
import pers.warren.ioc.inject.InjectField;


/**
 * 生命周期事件接入事件总线
 *
 *
 */
@Data
@NoArgsConstructor
@ToString
@Accessors(chain = true)
public class LifeCycleSignal extends AbstractSignal {

    private String beanName;

    private BeanDefinition beanDefinition;

    /**
     * 注入bean的名称
     */
    private String fieldBeanName;

    private InjectField valueField;

    private LifeCycleStep step;

    @Override
    public SignalType getType() {
        return SignalType.LIFE_CYCLE;
    }

    public LifeCycleSignal(BeanDefinition beanDefinition) {
        this.beanName = beanDefinition.getName();
        this.beanDefinition = beanDefinition;
    }
}
