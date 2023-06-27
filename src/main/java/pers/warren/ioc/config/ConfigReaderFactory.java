package pers.warren.ioc.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    private static final List<IConfigReader> readerList = new ArrayList<>();

    public static IConfigReader newConfigReader(String path){
        if(isJar(path)){
            JarConfigReader jarConfigReader = new JarConfigReader(path);
            readerList.add(jarConfigReader);
            return jarConfigReader;
        }else{
            LocalModuleConfigReader localModuleConfigReader = new LocalModuleConfigReader(path);
            readerList.add(localModuleConfigReader);
            return localModuleConfigReader;
        }
    }
}
