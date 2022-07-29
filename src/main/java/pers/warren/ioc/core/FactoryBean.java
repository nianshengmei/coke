package pers.warren.ioc.core;

/**
 * FactoryBean定义接口
 *
 * @author warren
 * @since jdk1.8
 */
public interface FactoryBean<T> {

    /**
     * 生成bean对象
     */
    <T> T getObject();

    /**
     * 获取bean的类型
     */
    Class<T> getType();

    /**
     * 是否单例
     */
    Boolean isSingleton();
}
