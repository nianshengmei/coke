package pers.warren.ioc.core;

import cn.hutool.core.util.ClassLoaderUtil;
import pers.warren.ioc.annotation.Component;
import pers.warren.ioc.annotation.Configuration;
import pers.warren.ioc.enums.BeanType;

/**
 * 默认的BeanRegister
 * @author warren
 */
public class DefaultBeanRegister implements BeanRegister {

    @Override
    public String getName(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        String beanName = null;
        boolean hasConfiguration = metadata.hasAnnotation(Configuration.class);
        if (hasConfiguration) {
            Configuration configuration = (Configuration) metadata.getAnnotation(Configuration.class);
            beanName = realBeanName(metadata, registry, configuration.name(), configuration.value());
        }

        boolean hasComponent = metadata.hasAnnotation(Component.class);
        if (hasComponent) {
            Component component = (Component) metadata.getAnnotation(Component.class);
            beanName = realBeanName(metadata, registry, component.name(), component.value());
        }
        return beanName;
    }


    @Override
    public BeanDefinition initialization(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        if(metadata.isAnnotation() || metadata.hasAnnotation("org.aspectj.lang.annotation.Aspect")){
            return null;
        }
        BeanDefinition beanDefinition = null;
        BeanDefinitionBuilder builder = null;
        if (metadata.hasAnnotation(Configuration.class)) {
            String name = getName(metadata, registry);
            builder = BeanDefinitionBuilder.genericBeanDefinition(metadata.getSourceClass(),
                    name,
                    BeanType.CONFIGURATION,
                    null,
                    name
            );
            builder.setRegister(this);
            beanDefinition = builder.build();
        } else if (metadata.hasAnnotation(Component.class)) {
            String name = getName(metadata, registry);
            builder = BeanDefinitionBuilder.genericBeanDefinition(metadata.getSourceClass(),
                    name,
                    BeanType.COMPONENT,
                    null,
                    name
            );
            builder.setRegister(this);
            beanDefinition = builder.build();
        }
        if (null == beanDefinition) {
            return null;
        }
        registry.registerBeanDefinition(beanDefinition.getName(), beanDefinition);
        return beanDefinition;
    }


}
