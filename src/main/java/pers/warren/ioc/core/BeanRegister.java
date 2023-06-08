package pers.warren.ioc.core;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import pers.warren.ioc.event.LifeCycleSignal;
import pers.warren.ioc.event.LifeCycleStep;;

import java.util.List;

/**
 * Bean的创造器
 * <p>
 * 自定义注解必要实现
 *
 * @author warren
 * @since 1.0.0
 */
public interface BeanRegister {

    /**
     * 获取bean名称
     *
     * @param importingClassMetadata 注解元数据
     * @param registry               注册器
     * @since 1.0.0
     */
    String getName(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry);

    /**
     * 获取bean的最终真实名称
     *
     * @param metadata 注解元数据
     * @param registry 注册器
     * @param name     名称
     * @param value    值
     * @return bean名称
     * @since 1.0.0
     */
    default String realBeanName(AnnotationMetadata metadata, BeanDefinitionRegistry registry, String name, String value) {
        String beanName = null;
        boolean nameOrValue = false;
        if (StrUtil.isNotEmpty(name)) {
            beanName = name;
            nameOrValue = true;
        } else if (StrUtil.isNotEmpty(value)) {
            beanName = value;
            nameOrValue = true;
        } else {
            beanName = metadata.getSourceClass().getSimpleName();
        }
        if (nameOrValue && registry.isBeanNameInUse(beanName)) {
            throw new RuntimeException("bean name in used : " + beanName);
        } else {
            while (registry.isBeanNameInUse(beanName)) {
                beanName += ("&" + RandomUtil.randomString(7));
            }
        }
        return beanName;
    }

    /**
     * 生成bean定义
     *
     * @param importingClassMetadata 注解元数据
     * @param registry               注册器
     * @since 1.0.0
     */
    BeanDefinition initialization(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry);

    /**
     * 注册BeanDefinition
     *
     * @param beanDefinition bean定义
     * @param registry       注册器
     * @since 1.0.0
     */
    default void registerBeanDefinition(BeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
        if (null == beanDefinition) {
            return;
        }
        registry.registerBeanDefinition(beanDefinition.getName(), beanDefinition);
        List<BeanPostProcessor> postProcessors = Container.getContainer().getBeans(BeanPostProcessor.class);
        if (0 == beanDefinition.getStep()) {
            for (BeanPostProcessor postProcessor : postProcessors) {
                postProcessor.postProcessBeforeInitialization(beanDefinition, this);
            }
            registry.runEvent(new LifeCycleSignal(beanDefinition).setStep(LifeCycleStep.BEFORE_PROCESSOR), beanDefinition.getBeforeProcessorEvent());
            beanDefinition.setStep(1);
        }

    }

}
