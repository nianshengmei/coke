package pers.warren.ioc.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import pers.warren.ioc.enums.BeanType;
import pers.warren.ioc.handler.CokePropertiesHandler;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class Container implements BeanDefinitionRegistry , CokeEnvironment{

    /**
     * 从 application.yml,application.properties中读取的配置文件信息
     * <p>
     * application.properties优先级 > application.yml
     */
    private Map<String, Object> propertiesMap = new HashMap<>();

    private final Map<String, Object> componentMap = new HashMap<>();

    private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    /**
     * 获取特定配置属性
     */
    @Override
    public Object getProperty(String k) {
        return this.propertiesMap.get(k);
    }

    /**
     * 添加单个属性
     */
    @Override
    public void addProperty(String k, Object v) {
        this.propertiesMap.put(k, v);
    }

    /**
     * 添加多个属性
     */
    @Override
    public void addProperties(Map<String, Object> source) {
        this.propertiesMap.putAll(source);
    }

    /**
     * 清空配置文件
     */
    @Override
    public void clearProperties() {
        this.propertiesMap = null;
    }

    @Override
    public void refreshProperties() {
        CokePropertiesHandler.read();
    }


    public void addComponent(String name, Object o) {
        componentMap.put(name, o);
        /* 后面的是对后置拦截的补偿 */
        BeanDefinition beanDefinition = beanDefinitionMap.get(name);
        if (null == beanDefinition) {
            return;
        }
        List<BeanPostProcessor> postProcessors = Container.getContainer().getBeans(BeanPostProcessor.class);
        for (BeanPostProcessor postProcessor : postProcessors) {
            postProcessor.postProcessAfterInitialization(beanDefinition, beanDefinition.getRegister());
        }
    }

    public boolean hasEqualComponent(Class<?> clz) {
        Collection<Object> values = componentMap.values();
        for (Object value : values) {
            if (value.getClass().getTypeName().equals(clz.getTypeName())) {
                return true;
            }
        }
        return false;
    }

    private static Container container;

    private final Map<String, List<InputStream>> propertiesIsMap = new HashMap<>();

    public Map<String, List<InputStream>> getPropertiesIsMap() {
        return propertiesIsMap;
    }

    public void addPropertiesIs(String name, InputStream is) {
        if (!propertiesIsMap.containsKey(name)) {
            propertiesIsMap.put(name, new ArrayList<>());
        }
        propertiesIsMap.get(name).add(is);
    }

    public static Container getContainer() {
        if (null == container) {
            container = new Container();
        }
        return container;
    }

    public ApplicationContext applicationContext() {
        return this.getBean(ApplicationContext.class);
    }

    private Container() {

    }

    public List<BeanDefinition> getBeanDefinitions(BeanType beanType) {
        return beanDefinitionMap.values().stream().filter(d -> beanType == d.getBeanType()).collect(Collectors.toList());
    }


    public <T> T getBean(String name) {
        return (T) componentMap.get(name);
    }

    @Override
    public Collection<BeanWrapper> getBeanWrappers() {
        Collection<BeanWrapper> wrappers = new ArrayList<>();
        Collection<BeanDefinition> values = beanDefinitionMap.values();
        for (BeanDefinition key : values) {
            wrappers.add(new BeanWrapper().setName(key.getName()).setBeanDefinition(key)
                    .setClz(key.clz));
        }
        return wrappers;
    }

    public <T> T getBean(Class<T> clz) {
        List<T> beans = getBeans(clz);
        return CollUtil.isNotEmpty(beans) ? beans.get(0) : null;
    }

    public <T> List<T> getBeans(Class<T> clz) {
        Collection<Object> values = componentMap.values();
        List<T> tList = new ArrayList<>();
        for (Object bean : values) {
            try {
                if (clz.isAssignableFrom(bean.getClass())) {
                    tList.add((T) bean);
                }
            } catch (Exception e) {
                continue;
            }

        }
        return tList;
    }

    @Override
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
        if (StrUtil.isNotEmpty(name) && null != beanDefinition) {
            this.beanDefinitionMap.put(name, beanDefinition);
        }
    }

    @Override
    public void removeBeanDefinition(String name) {
        this.beanDefinitionMap.remove(name);
    }

    @Override
    public BeanDefinition getBeanDefinition(String name) {
        return this.beanDefinitionMap.get(name);
    }

    public BeanDefinition getBeanDefinition(Class<?> clz) {
        Collection<BeanDefinition> values = beanDefinitionMap.values();
        for (BeanDefinition value : values) {
            Class<?> aClass = value.getClz();
            if (clz.isAssignableFrom(aClass)) {
                return value;
            }
        }
        return null;
    }

    public List<BeanDefinition> getBeanDefinitions(Class<?> clz) {
        List<BeanDefinition> dfs = new ArrayList<>();
        Collection<BeanDefinition> values = beanDefinitionMap.values();
        for (BeanDefinition value : values) {
            Class<?> aClass = value.getClz();
            if (clz.isAssignableFrom(aClass)) {
                dfs.add(value);
            }
        }
        return dfs;
    }

    public Collection<BeanDefinition> getBeanDefinitions() {
        return beanDefinitionMap.values();
    }

    @Override
    public boolean containsBeanDefinition(String name) {
        return beanDefinitionMap.containsKey(name);
    }

    @Override
    public String[] getBeanDefinitionNames() {
        String[] strings = new String[beanDefinitionMap.size()];
        return beanDefinitionMap.keySet().toArray(strings);
    }

    @Override
    public int getBeanDefinitionCount() {
        return beanDefinitionMap.size();
    }

    @Override
    public boolean isBeanNameInUse(String name) {
        return beanDefinitionMap.containsKey(name) || componentMap.containsKey(name);
    }

    public void addBeanDefinition(BeanDefinition beanDefinition) {
        beanDefinitionMap.put(beanDefinition.name, beanDefinition);
    }
}
