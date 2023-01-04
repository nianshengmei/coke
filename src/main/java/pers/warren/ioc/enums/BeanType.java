package pers.warren.ioc.enums;

/**
 * Bean的类型
 *
 * @author warren
 * @since jdk8
 */
public enum BeanType {

    /**
     * 容器上下文
     */
    CONTEXT,

    /**
     * 基础组件
     */
    BASE_COMPONENT,

    /**
     * 简单bean
     * 一般是由函数声明的Bean,例如@Bean修饰的方法创建出的Bean
     */
    SIMPLE_BEAN,

    /**
     * 组件
     * 一般是由类声明的Bean,例如@Component修饰的方法创建出的Bean
     */
    COMPONENT,

    /**
     * 配置
     * 仅限于@Confguration,也可以扩展，但需要支持包扫描，一般不建议
     */
    CONFIGURATION,

    PROXY,

    OTHER
    ;
}
