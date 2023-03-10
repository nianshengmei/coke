package pers.warren.ioc.core.bean;

/**
 * bean的包装类
 */
public interface BeanWrapper {

    String getBeanName();

    Class<?> getBeanClass();

    Boolean isProxy();

    Boolean isSingleton();

}
