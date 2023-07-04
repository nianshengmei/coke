package pers.warren.ioc.event;

/**
 * 生命周期阶段
 *
 * @author warren
 * @since 1.0.1
 */
public enum LifeCycleStep {

    /**
     * bean注册
     * {@link pers.warren.ioc.core.BeanRegister#initialization}
     * <p>该阶段会遍历扫描范围的所有class,并将符合生成规则的类或者方法，或者其他自定义的一些信号，初始化为BeanDefinition。</p>
     * <p>该阶段也是coke允许生成BeanDefinition的三大阶段之一，也是最主要的阶段。</p>
     * <p>注意:只有有BeanDefinition，才能生成bean,当然也存在只有BeanDefinition，却在生成时被condition或者排除器限制拒绝生成的情况。</p>
     * <p>
     * Bean registration
     * <p>
     * At this stage, all classes within the scanning range will be traversed and classes or methods that comply with the generation rules, or other customized signals, will be initialized as BeanDefinition.
     * <p>
     * This stage is also one of the three major stages in which COKE allows for the generation of BeanDefinitions, and it is also the most important stage.
     * <p>
     * Note: Only with BeanDefinition can beans be generated, and of course, there are also cases where only BeanDefinition is generated but is restricted by conditions or exclusions during generation.
     *
     * @author warren
     * @since 1.0.2
     */
    REGISTER,

    /**
     * bean的前置处理
     *
     * {@link pers.warren.ioc.core.BeanPostProcessor#postProcessBeforeInitialization}
     * <p>该阶段由BeanPostProcessor的postProcessBeforeInitialization方法发起。</p>
     * <p>该阶段是生成BeanDefinition的唯三阶段之一。</p>
     * <p>@Bean所定义的bean就是在该阶段生成BeanDefinition。</p>
     *
     * @author warren
     * @since 1.0.2
     */
    BEFORE_PROCESSOR,

    /**
     * bean的后置处理
     *
     * <p>到该阶段为止,BeanDefinition还未生成bean,因此该阶段同样是创建BeanDefinition的时机。</p>
     * <p>Aop的ProxyBeanDefinition就是在该阶段创建的。</p>
     * <p>该阶段结束，标志着bean即将被生成创建。</p>
     *
     * @author warren
     * @since 1.0.2
     */
    AFTER_PROCESSOR,

    /**
     * bean生成的后置
     *
     * <p>在该阶段之前,bean已经被创建。</p>
     * <p>在该阶段会注入一些配置文件。bean创建的后置方法的调用{@link javax.annotation.PostConstruct}</p>
     *
     * @author warren
     */
    AFTER_INITIALIZATION,

    /**
     * bean注入
     *
     * @auther warren
     * @since 1.0.2
     */
    BEAN_INJECT
}
