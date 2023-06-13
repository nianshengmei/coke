package pers.warren.ioc.loader;

import pers.warren.ioc.handler.CokePropertiesHandler;

/**
 * 配置文件加载器
 *
 * @author warren
 * @since 1.0.0
 */

public class PropertiesLoader implements Loader{
    @Override
    public boolean load(Class<?> clz) {
        CokePropertiesHandler.read();   //读取配置文件
        return true;
    }
}
