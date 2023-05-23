package pers.warren.ioc.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import pers.warren.ioc.ec.WarnEnum;
import pers.warren.ioc.ec.WithoutNoParamConstructorException;
import pers.warren.ioc.enums.BeanType;
import pers.warren.ioc.event.*;
import pers.warren.ioc.handler.CokePropertiesHandler;
import pers.warren.ioc.loader.LoadPair;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class Container implements BeanDefinitionRegistry, Environment {

    /**
     * 从 application.yml,application.properties中读取的配置文件信息
     * <p>
     * application.properties优先级 > application.yml
     */
    private Map<String, Object> propertiesMap = new HashMap<>();

    /**
     * 存放bean的Map
     */
    private final Map<String, Object> componentMap = new TreeMap<>();

    /**
     * 存放bean定义
     */
    private final Map<String, BeanDefinition> beanDefinitionMap = new TreeMap<>();

    /**
     * 委托coke寻找的类(一般是给出接口或类,寻找其所有实现类)
     */
    private final Map<String, List<Class<?>>> findClassMap = new HashMap<>();

    /**
     * 排除器,用于启动时排除某个bean
     */
    @Getter
    private final Eliminator eliminator = new Eliminator();

    /**
     * 用于因为@Before和@After导致的循环优先加载问题
     */
    @Getter
    private final Set<LoadPair> pairs = new HashSet<>();

    /**
     * k beanName
     * v @Lazy标注的bean的真实bean
     *
     * 该字段的左右是为了给@lazy标注的字段注入属性和配置
     */
    @Getter
    private Map<String,Object> lazyBeanMap = new HashMap<>();

    public boolean containsPair(LoadPair pair){
        return pairs.contains(pair);
    }

    public <R> List<R> findClass(Class<?> clz) {
        return (List<R>)findClassMap.get(clz.getTypeName());
    }

    public void addFindClass(Class<?> clz,Class<?> c){
        findClassMap.putIfAbsent(clz.getTypeName(),new ArrayList<>());
        findClassMap.get(clz.getTypeName()).add(c);
    }

    /**
     * 获取特定配置属性
     */
    @Override
    public Object getProperty(String k) {
        String env = System.getenv(k);
        if (StrUtil.isNotEmpty(env)) {
            return env;
        }
        return this.propertiesMap.get(k);
    }

    /**
     * 判断是否存在特定配置属性
     */
    public boolean containsProperties(String k) {
        String env = System.getenv(k);
        if (StrUtil.isNotEmpty(env)) {
            return true;
        }
        return this.propertiesMap.containsKey(k);
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
        BeanDefinition beanDefinition = beanDefinitionMap.get(StrUtil.lowerFirst(name));
        if (null == beanDefinition || beanDefinition.getStep() != 2) {
            return;
        }
        componentMap.put(StrUtil.lowerFirst(name), o);

    }

    public void addPreloadComponent(String name, Object o){
        BeanDefinition beanDefinition = beanDefinitionMap.get(StrUtil.lowerFirst(name));
        if (null == beanDefinition || beanDefinition.getStep() != 0) {
            return;
        }
        componentMap.put(StrUtil.lowerFirst(name), o);
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
        if(lazyBeanMap.containsKey(name)){
            return (T) lazyBeanMap.get(StrUtil.lowerFirst(name));
        }
        return (T) componentMap.get(StrUtil.lowerFirst(name));
    }

    /**
     * 判断当前运行时是否支持AOP
     */
    public boolean isAopEnvironment() {
        Object proxyApplicationContext = this.getBean("ProxyApplicationContext");
        String typeName = proxyApplicationContext.getClass().getTypeName();
        if (null != proxyApplicationContext
                && typeName.equals("org.needcoke.coke.aop.core.ProxyApplicationContext")
                && proxyApplicationContext instanceof ApplicationContext) {
            return true;
        }
        return false;
    }

    /**
     * 判断当前运行时是否web环境
     */
    public boolean isWebEnvironment() {
        Object webApplicationContext = this.getBean("webApplicationContext");
        String typeName = webApplicationContext.getClass().getTypeName();
        if (null != webApplicationContext
                && typeName.equals("org.needcoke.coke.web.core.WebApplicationContext")
                && webApplicationContext instanceof ApplicationContext) {
            return true;
        }
        return false;
    }

    public Object getSimpleBean(String name) {
        if (name.endsWith("#proxy")) {
            name = StrUtil.removeSuffix(name, "#proxy");
        }
        return getBean(name);
    }

    @Override
    public Collection<BeanWrapper> getBeanWrappers() {
        Collection<BeanWrapper> wrappers = new ArrayList<>();
        Collection<BeanDefinition> values = beanDefinitionMap.values();
        for (BeanDefinition key : values) {
            if (!key.isProxy()) {
                wrappers.add(
                        new BeanWrapper()
                                .setName(StrUtil.lowerFirst(key.getName()))
                                .setBeanDefinition(key)
                                .setClz(key.clz));
            }
        }
        return wrappers;
    }

    public <T> T getBean(Class<T> clz) {
        List<BeanDefinition> beanDefinitions= getBeanDefinitions(clz);
        if(CollUtil.isNotEmpty(beanDefinitions)){
            return getBean(beanDefinitions.get(0).getName());
        }
        return null;
    }

    public <T> List<T> getBeans(Class<T> clz) {
        Collection<Object> values = componentMap.values();
        List<T> tList = new CopyOnWriteArrayList<>();
        for (Object bean : values) {
            try {
                if (null == bean) {
                    continue;
                }
                if (clz.isAssignableFrom(bean.getClass()) || clz.getTypeName().equals(bean.getClass().getTypeName())) {
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
        //排除指定bean
        Eliminator eliminator = Container.getContainer().getEliminator();
        if (eliminator.isExclude(beanDefinition.getClz()) || eliminator.isExclude(name)) {
            return;
        }
        if (StrUtil.isNotEmpty(name) && null != beanDefinition) {
            beanDefinition.setName(StrUtil.lowerFirst(beanDefinition.getName()));

            AnnotationMetadata annotationMetadata = beanDefinition.getAnnotationMetadata();
            if (annotationMetadata.hasAnnotation(LifeCycleEvent.class)) {
                LifeCycleEvent annotation = (LifeCycleEvent) annotationMetadata.getAnnotation(LifeCycleEvent.class);
                beanDefinition.setRegisterEvent(Arrays.asList(annotation.register()));
                beanDefinition.setBeforeProcessorEvent(Arrays.asList(annotation.beforeProcessor()));
                beanDefinition.setAfterProcessorEvent(Arrays.asList(annotation.afterProcessor()));
                beanDefinition.setWhenFieldInjectEvent(Arrays.asList(annotation.whenFieldInject()));
                beanDefinition.setAfterInitializationEvent(Arrays.asList(annotation.afterInitialization()));
            }
            this.beanDefinitionMap.put(StrUtil.lowerFirst(name), beanDefinition);
            runEvent(new LifeCycleSignal(beanDefinition).setStep(LifeCycleStep.REGISTER), beanDefinition.registerEvent);
        }
    }

    @Override
    public void removeBeanDefinition(String name) {
        this.beanDefinitionMap.remove(StrUtil.lowerFirst(name));
    }

    @Override
    public BeanDefinition getBeanDefinition(String name) {
        return this.beanDefinitionMap.get(StrUtil.lowerFirst(name));
    }

    public BeanDefinition getProxyBeanDefinition(String name) {
        if (name.endsWith("#proxy")) {
            name = StrUtil.removeSuffix(name, "#proxy");
        }
        return this.beanDefinitionMap.get(getProxyBeanName(name));
    }

    private String getProxyBeanName(String name) {
        return StrUtil.lowerFirst(name) + "#proxy";
    }

    public boolean isProxyBeanDefinition(BeanDefinition beanDefinition) {
        return beanDefinition.getName().endsWith("#proxy");
    }

    public boolean containsProxyBean(String name) {
        return this.componentMap.containsKey(getProxyBeanName(name));
    }

    public boolean containsProxyBeanDefinition(String name) {
        return this.beanDefinitionMap.containsKey(getProxyBeanName(name));
    }

    public boolean containsProxyBean(Class<?> clz) {
        List<BeanDefinition> beanDefinitions = getBeanDefinitions(clz);
        for (BeanDefinition beanDefinition : beanDefinitions) {
            if (beanDefinition.getClass().getTypeName().equals(clz.getTypeName())) {
                return true;
            }
        }
        return false;
    }


    public BeanDefinition getBeanDefinition(Class<?> clz) {
        return getBeanDefinition(clz, false);
    }

    public BeanDefinition getBeanDefinition(Class<?> clz, boolean proxy) {
        Collection<BeanDefinition> values = beanDefinitionMap.values();
        for (BeanDefinition value : values) {
            if (value.getName().endsWith("#proxy") && !proxy) {
                continue;
            }
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
        Collection<BeanDefinition> values = beanDefinitionMap.values();
        List<BeanDefinition> beanDefinitionCopyOnWriteArrayList = new ArrayList<>();
        beanDefinitionCopyOnWriteArrayList.addAll(values);
        return beanDefinitionCopyOnWriteArrayList;
    }

    @Override
    public boolean containsBeanDefinition(String name) {
        return beanDefinitionMap.containsKey(StrUtil.lowerFirst(name));
    }

    @Override
    public boolean containsBeanDefinition(Class<?> clz) {
        Collection<BeanDefinition> values = beanDefinitionMap.values();
        for (BeanDefinition value : values) {
            if (value.getClz().getTypeName().equals(clz.getTypeName())) {
                return true;
            }
        }
        return false;
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
    public int getBeanCount(Class<?> clz) {
        List<?> beans = getBeans(clz);
        return beans.size();
    }

    @Override
    public boolean isBeanNameInUse(String name) {
        return beanDefinitionMap.containsKey(StrUtil.lowerFirst(name)) || componentMap.containsKey(StrUtil.lowerFirst(name));
    }

    public void addBeanDefinition(BeanDefinition beanDefinition) {
        beanDefinition.setName(StrUtil.lowerFirst(beanDefinition.getName()));
        beanDefinitionMap.put(beanDefinition.name, beanDefinition);
    }

    public <T> T getProxyBean(String name) {
        return (T) componentMap.get(getProxyBeanName(name));
    }

    public <T> T getProxyBean(Class<T> clz) {
        Collection<BeanDefinition> beanDefinitions = getBeanDefinitions();
        for (BeanDefinition beanDefinition : beanDefinitions) {
            if (beanDefinition.getClass().getTypeName().equals("org.needcoke.coke.aop.core.ProxyBeanDefinition")) {
                return getBean(beanDefinition.getName());
            }
        }
        return null;
    }

    /**
     * 执行事件
     *
     * @param signal 信号
     *
     *
     */
    public void runEvent(Signal signal, List<Class<? extends Event>> eventClasses) {
        if (CollUtil.isEmpty(eventClasses)) {
            return;
        }
        for (Class<? extends Event> eventClz : eventClasses) {
            try {
                Constructor<? extends Event> constructor = eventClz.getConstructor();
                Event event = constructor.newInstance();
                event.doEvent(signal);
            } catch (Throwable e) {
                throw new WithoutNoParamConstructorException(WarnEnum.LIFE_CYCLE_EVENTS_MUST_HAVE_NON_PARAMETER_CONSTRUCTORS);
            }
        }
    }
}
