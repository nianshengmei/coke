package pers.warren.ioc.config;

import pers.warren.ioc.core.Container;

import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarConfigReader extends AbstractConfigReader {

    public JarConfigReader(String path) {
        super(path);
    }

    @Override
    public void read() {
        if(path.contains("rpc-module")){
            int i =0;
        }
        Container container = Container.getContainer();
        try {
            JarFile jarFile = new JarFile(path);
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String name = jarEntry.getName();
                if (name.charAt(0) == '/') {
                    name = name.substring(1);
                }
                if (name.endsWith(YML)) {
                    container.addPropertiesIs(YML, jarFile.getInputStream(jarEntry));
                } else if (name.endsWith(YAML)) {
                    container.addPropertiesIs(YAML, jarFile.getInputStream(jarEntry));
                } else if (name.endsWith(PROPERTIES)) {
                    container.addPropertiesIs(PROPERTIES, jarFile.getInputStream(jarEntry));
                } else if (name.endsWith("META-INF/coke.abc")) {
                    container.addPropertiesIs(COKE, jarFile.getInputStream(jarEntry));
                }
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
