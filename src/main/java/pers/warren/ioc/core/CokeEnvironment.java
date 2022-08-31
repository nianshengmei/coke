package pers.warren.ioc.core;

import java.util.Map;

public interface CokeEnvironment {

    /**
     * 获取特定配置属性
     */
    default Object getProperty(String k) {
        return Container.getContainer().getProperty(k);
    }

    /**
     * 添加单个属性
     */

    default void addProperty(String k, Object v) {
        Container.getContainer().addProperty(k, v);
    }

    /**
     * 添加多个属性
     */

    default void addProperties(Map<String, Object> source) {
        Container.getContainer().addProperties(source);
    }

    /**
     * 清空配置文件
     */

    default void clearProperties() {
        Container.getContainer().clearProperties();
    }

    /**
     * 重新加载配置文件
     */

    default void refreshProperties() {
        Container.getContainer().refreshProperties();
    }

}
