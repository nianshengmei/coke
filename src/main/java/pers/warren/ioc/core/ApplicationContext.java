package pers.warren.ioc.core;

import java.util.HashMap;
import java.util.Map;

public class ApplicationContext {

    /**
     * 从 application.yml,application.properties中读取的配置文件信息
     * <p>
     * application.properties优先级 > application.yml
     */
    private final Map<String, Object> propertiesMap = new HashMap<>();

    /**
     * 添加单个属性
     */
    public void addProperty(String k,Object v){
        propertiesMap.put(k,v);
    }
    /**
     * 添加多个属性
     */
    public void addProperties(Map<String, Object> source){
        propertiesMap.putAll(source);
    }


}
