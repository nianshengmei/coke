package pers.warren.ioc.util;

import cn.hutool.core.util.StrUtil;

/**
 * 字符串工具类
 *
 * @since 1.0.1
 */
public class StringUtil {

    /**
     * 代理bean名称后缀
     */
    private static final String PROXY_SUFFIX = "#proxy";

    /**
     * 是否是代理bean的名称
     *
     * @since 1.0.2
     */
    public static boolean isProxyName(String name) {
        return StrUtil.endWith(name, PROXY_SUFFIX);
    }

    /**
     * 获取代理bean的原始名称
     *
     * @since 1.0.2
     */
    public static String getOriginalName(String name) {
        return isProxyName(name) ? StrUtil.removeSuffix(name, PROXY_SUFFIX) : StrUtil.lowerFirst(name);
    }

    /**
     * 获取bean的代理名称
     *
     * @since 1.0.2
     */
    public static String getProxyName(String name) {
        return isProxyName(name) ? name : StrUtil.lowerFirst(name) + PROXY_SUFFIX;
    }

}
