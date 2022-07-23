package pers.warren.ioc.handler;

import cn.antcore.resources.extend.PropertiesResources;
import cn.antcore.resources.extend.YamlResources;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.CharUtil;
import lombok.experimental.UtilityClass;
import pers.warren.ioc.annotation.Configuration;
import pers.warren.ioc.annotation.Scanner;
import pers.warren.ioc.core.ApplicationContext;
import pers.warren.ioc.core.Container;
import pers.warren.ioc.util.ScanUtil;

import java.io.*;
import java.util.*;

@UtilityClass
public class CokePropertiesHandler {

    private static final String YAML = "application.yaml";
    private static final String YML = "application.yml";
    private static final String PROPERTIES = "application.properties";
    private static final String CONF = "application.conf";

    private static final String COKE = "META-INF" + File.separatorChar + "coke.abc";

    public void read() {
        try {
            beforeRead();
            readProperties();
            readYml();
            readYaml();
            readCoke();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void beforeRead() {
        ScanUtil.localFilePath.addAll(ScanUtil.jarFilePath);
        Set<String> localFilePath = ScanUtil.localFilePath;
        List<File> ymlList = new ArrayList<>();
        List<File> propertiesList = new ArrayList<>();
        List<File> metaInfoFileList = new ArrayList<>();
        for (String path : localFilePath) {
            File f = new File(path);
            if (f.isDirectory()) {
                File[] files = f.listFiles(i -> i.isFile() && (i.getName().endsWith(".yml") || i.getName().endsWith(".yaml")));
                ymlList.addAll(Arrays.asList(files));

                File[] fs = f.listFiles(i -> i.isFile() && (i.getName().endsWith(".properties")));
                propertiesList.addAll(Arrays.asList(fs));

                File[] metaInfoFiles = f.listFiles(i -> i.getName().equals("META-INF"));

                for (File metaInfoFile : metaInfoFiles) {
                    if (metaInfoFile.isDirectory()) {

                        metaInfoFileList.addAll(
                                Arrays.asList(
                                        metaInfoFile.listFiles(i -> i.isFile())
                                ));
                    }
                }
            }
        }

        Container container = Container.getContainer();
        Map<String, List<InputStream>> propertiesIsMap = container.getPropertiesIsMap();
        for (File file : ymlList) {
            String name = file.getName();
            if (!propertiesIsMap.containsKey(name)) {
                propertiesIsMap.put(name, new ArrayList<>());
            }
            try {
                propertiesIsMap.get(name).add(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        for (File file : propertiesList) {
            String name = file.getName();
            if (!propertiesIsMap.containsKey(name)) {
                propertiesIsMap.put(name, new ArrayList<>());
            }
            try {
                propertiesIsMap.get(name).add(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        for (File file : metaInfoFileList) {
            String name = file.getName();
            name = "META-INFO/" + name;
            if (!propertiesIsMap.containsKey(name)) {
                propertiesIsMap.put(name, new ArrayList<>());
            }
            try {
                propertiesIsMap.get(name).add(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        int i = 0;
    }

    public void readYml() throws IOException {
        YamlResources resources = new YamlResources();
        Container container = Container.getContainer();
        List<InputStream> inputStreams = container.getPropertiesIsMap().get(YML);
        Map<Object, Object> resourceMap = new HashMap<>();
        for (InputStream inputStream : inputStreams) {
            resources.load(inputStream);
        }
        resourceMap.putAll(resources.getResources());
        Map<String, Object> standardization = standardization(resourceMap);
        ApplicationContext context = Container.getContainer().getBean(ApplicationContext.class.getSimpleName());
        context.addProperties(standardization);
    }

    public void readYaml() throws IOException {
        YamlResources resources = new YamlResources();
        Container container = Container.getContainer();
        List<InputStream> inputStreams = container.getPropertiesIsMap().get(YAML);
        if(CollUtil.isEmpty(inputStreams)){
            return;
        }
        if(CollUtil.isEmpty(inputStreams)){
            return;
        }
        Map<Object, Object> resourceMap = new HashMap<>();
        for (InputStream inputStream : inputStreams) {
            resources.load(inputStream);
        }
        resourceMap.putAll(resources.getResources());
        Map<String, Object> standardization = standardization(resourceMap);
        ApplicationContext context = Container.getContainer().getBean(ApplicationContext.class.getSimpleName());
        context.addProperties(standardization);
        int a = 1;
    }

    public void readProperties() throws IOException {
        PropertiesResources resources = new PropertiesResources();
        Container container = Container.getContainer();
        List<InputStream> inputStreams = container.getPropertiesIsMap().get(PROPERTIES);
        if(CollUtil.isEmpty(inputStreams)){
            return;
        }
        Map<Object, Object> resourceMap = new HashMap<>();
        for (InputStream inputStream : inputStreams) {
            resources.load(inputStream);
        }
        resourceMap.putAll(resources.getResources());
        Map<String, Object> standardization = standardization(resourceMap);
        ApplicationContext context = Container.getContainer().getBean(ApplicationContext.class.getSimpleName());
        context.addProperties(standardization);
    }

    public void readCoke() {
        String content = ResourceUtil.readUtf8Str(COKE);
        StringBuilder tmp = new StringBuilder();
        for (int i = 0; i < content.length(); i++) {
            if (!CharUtil.isBlankChar(content.charAt(i))) {
                tmp.append(content.charAt(i));
            }
        }
        content = tmp.toString();
        String[] split = content.split("=\\{");
        for (int i = 0; i < split.length; i += 2) {
            if (split[i].equals(Configuration.class.getName())) {
                String configs = split[i + 1].replace("}", "");
                String[] cs = configs.split(",");
                for (String c : cs) {
                    try {
                        Class<?> clz = Class.forName(c);
                        Configuration configurationAnnotation = clz.getAnnotation(Configuration.class);
                        if (configurationAnnotation != null) {
                            ScanUtil.scanPackageFor(clz.getPackage().getName());
                            ScanUtil.scanArray(configurationAnnotation.scanner());

                            Scanner scannerAnnotation = clz.getAnnotation(Scanner.class);
                            if (scannerAnnotation != null) {
                                ScanUtil.scanArray(scannerAnnotation.value());
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        }
    }

    /**
     * 用于描述配置数组的内部类
     */
    private static class ConfigObject implements Comparable<ConfigObject> {
        public int index;

        public Object v;

        @Override
        public int compareTo(ConfigObject o) {
            return Integer.compare(this.index, o.index);
        }
    }


    /**
     * 标准化结果
     * <p>
     * 处理resourceMap中未处理的数组型配置文件为 Object[]
     *
     * @param resourceMap resources.getResources()的结果
     * @return 标准化的结果
     * @author warren
     */
    public Map<String, Object> standardization(Map<Object, Object> resourceMap) {
        Set<Object> keySet = resourceMap.keySet();
        Map<String, Object> configMap = new HashMap<>();
        for (Object o : keySet) {
            String key = (String) o;
            if (key.contains("[")) {
                String[] split = key.split("\\[");
                String K = split[0];
                String b = split[1].replace("]", "");
                int I = Integer.parseInt(b);

                if (!configMap.containsKey(K)) {
                    configMap.put(K, new ArrayList<>());
                }
                Object o1 = configMap.get(K);
                if (o1 instanceof List) {
                    ConfigObject configObject = new ConfigObject();
                    configObject.index = I;
                    configObject.v = resourceMap.get(key);
                    ((List) o1).add(configObject);
                }
            } else {
                if (configMap.containsKey(key)) {
                    throw new RuntimeException("重复定义 property , property key " + key);
                } else {
                    configMap.put(key, resourceMap.get(key));
                }
            }
        }

        Set<String> ks = configMap.keySet();
        Map<String, Object> cm = new HashMap<>();
        for (String k : ks) {
            Object o = configMap.get(k);
            if (o instanceof List) {
                if (((List) o).size() > 0 && ((List) o).get(0) instanceof ConfigObject) {
                    List<ConfigObject> list = (List) o;
                    Collections.sort(list);
                    Object[] vs = new Object[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        vs[i] = list.get(i).v;
                    }
                    cm.put(k, vs);
                } else {
                    cm.put(k, ((List) o).toArray());
                }
            } else {
                cm.put(k, configMap.get(k));
            }
        }
        return cm;
    }
}
