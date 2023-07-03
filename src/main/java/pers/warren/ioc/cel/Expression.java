package pers.warren.ioc.cel;

/**
 * 表达式接口
 *
 * @since warren
 *
 * @since 1.0.3
 */
public interface Expression {

    /**
     * 获取值
     *
     * @since 1.0.3
     */
    <T> T getValue(Class<T> clazz);
}
