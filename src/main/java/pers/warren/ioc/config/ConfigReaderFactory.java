package pers.warren.ioc.config;

import java.io.File;

public class ConfigReaderFactory {


    /**
     * 是否是jar包
     */
    private static boolean isJar(String path){
        return path.endsWith(".jar") && new File(path).isFile();
    }

    /**
     * 是否是本地的模块
     */
    private static boolean isLocalModule(String path){
        return new File(path).isDirectory() ;
    }

    public static IConfigReader newConfigReader(String path){
        if(isJar(path)){
            return new JarConfigReader(path);
        }else{
            return new LocalModuleConfigReader(path);
        }
    }
}
