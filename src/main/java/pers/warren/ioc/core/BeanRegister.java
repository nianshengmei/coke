package pers.warren.ioc.core;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import pers.warren.ioc.event.LifeCycleStep;
import pers.warren.ioc.event.Signal;

import java.util.List;

/**
 * Bean的创造器
 * <p>
 * 自定义注解必要实现
 *
 * @author warren
 * @since jdk1.8
 */
public interface BeanRegister {

    String getName(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry);


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
                beanName += ("#" + RandomUtil.randomString(7));
            }
        }
        return beanName;
    }

    BeanDefinition initialization(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry);

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
            registry.runEvent(new Signal(beanDefinition).setStep(LifeCycleStep.BEFORE_PROCESSOR), beanDefinition.getBeforeProcessorEvent());
            beanDefinition.setStep(1);
        }

    }

}
