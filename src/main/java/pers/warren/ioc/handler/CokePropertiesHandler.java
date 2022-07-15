package pers.warren.ioc.handler;

import cn.antcore.resources.extend.PropertiesResources;
import java.io.IOException;
import java.util.Map;

public class CokePropertiesHandler {

    private static final String YAML = "application.yaml";
    private static final String YML = "application.yml";
    private static final String PROPERTIES = "application.properties";
    private static final String CONF = "application.conf";

    public void read(){
        try{

            readProperties();
        }catch (Exception e){

        }

    }

    public void readYaml(){

    }

    public void readProperties() throws IOException {
        PropertiesResources resources = new PropertiesResources();
        resources.loadByClassPath(PROPERTIES);
        Map<Object, Object> resources1 = resources.getResources();
        int a = 1;
    }

    public void readConf(){

    }
}
