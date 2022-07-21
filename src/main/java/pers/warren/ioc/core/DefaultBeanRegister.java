package pers.warren.ioc.core;

import pers.warren.ioc.annotation.Configuration;
import pers.warren.ioc.enums.BeanType;

public class DefaultBeanRegister implements BeanRegister {


    @Override
    public String getName(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        String beanName = null;
        boolean hasConfiguration = metadata.hasAnnotation(Configuration.class);
        if (hasConfiguration) {
            Configuration configuration = (Configuration) metadata.getAnnotation(Configuration.class);
            beanName = realBeanName(metadata, registry, configuration.name(), configuration.value());
        }
        return beanName;
    }


    @Override
    public BeanDefinition initialization(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
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
            beanDefinition = builder.build();
        }
        registerBeanDefinition(builder, registry);
        return beanDefinition;
    }


}
