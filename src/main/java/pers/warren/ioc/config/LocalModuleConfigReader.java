package pers.warren.ioc.config;

import cn.hutool.core.io.FileUtil;
import pers.warren.ioc.core.Container;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class LocalModuleConfigReader extends AbstractConfigReader {

    public LocalModuleConfigReader(String path) {
        super(path);
    }

    @Override
    public void read() {
        Container container = Container.getContainer();
        FileUtil.loopFiles(path, APP_YAML_FILTER).forEach(i -> {
            try {
                container.addPropertiesIs(YAML, new FileInputStream(i));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        FileUtil.loopFiles(path, APP_YML_FILTER).forEach(i -> {
            try {
                container.addPropertiesIs(YML, new FileInputStream(i));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        FileUtil.loopFiles(path, APP_PROPERTIES_FILTER).forEach(i -> {
            try {
                container.addPropertiesIs(PROPERTIES, new FileInputStream(i));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });


        FileUtil.loopFiles(path, COKE_FILTER).forEach(i -> {
            try {
                container.addPropertiesIs(COKE, new FileInputStream(i));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static final FileFilter APP_YML_FILTER = i -> i.getAbsolutePath().endsWith(YML);

    private static final FileFilter APP_YAML_FILTER = i -> i.getAbsolutePath().endsWith(YAML);

    private static final FileFilter APP_PROPERTIES_FILTER = i -> i.getAbsolutePath().endsWith(PROPERTIES);

    private static final FileFilter COKE_FILTER = i -> i.getAbsolutePath().endsWith(COKE);
}
