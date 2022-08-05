package pers.warren.ioc.config;

import java.io.File;

public interface IConfigReader {

    /**
     * 绝对路径
     */
    String getPath();

    void read();

    String YAML = "application.yaml";

    String YML = "application.yml";

    String PROPERTIES = "application.properties";

    String COKE = "META-INF" + File.separatorChar + "coke.abc";

}
