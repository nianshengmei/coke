package pers.warren.ioc.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import pers.warren.ioc.ec.WarnEnum;
import pers.warren.ioc.ec.WithoutNoParamConstructorException;
import pers.warren.ioc.enums.BeanType;
import pers.warren.ioc.event.*;
import pers.warren.ioc.event.EventListener;
import pers.warren.ioc.handler.CokePropertiesHandler;
import pers.warren.ioc.loader.LoadPair;
import pers.warren.ioc.util.StringUtil;

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
     * <p>
     * 该字段的左右是为了给@lazy标注的字段注入属性和配置
     */
    @Getter
    private final Map<String, Object> lazyBeanMap = new HashMap<>();

    /**
     * 是否存在这样的一个加载规则
     *
     * <p>用于解决循环依赖问题</p>
     *
     * @param pair 因为某个bean而加载bean的规则对
     * @since 1.0.1
     */
    public boolean containsPair(LoadPair pair) {
        return pairs.contains(pair);
    }

    /**
     * 查找预先让coke寻找的类的子类
     *
     * @param clz 类
     * @since 1.0.1
     */
    @SuppressWarnings("unchecked")
    public <R> List<R> findClass(Class<?> clz) {
        return (List<R>) findClassMap.get(clz.getTypeName());
    }

    /**
     * 事件监听器  key:事件类型 value:事件监听器实例（可能有多个,可能是容器中的bean）
     *
     * @since 1.0.2
     */
    private final Map<String, List<Object>> listenerMap = new HashMap<>();

    /**
     * CEL表达式模板
     *
     * @since 1.0.3
     */
    @Getter
    @Setter
    private String celTemplate;

    /**
     * CEL bean表达式模板
     *
     * @since 1.0.3
     */
    @Getter
    @Setter
    private String celBeanTemplate;

    public void addFindClass(Class<?> clz, Class<?> c) {
        findClassMap.putIfAbsent(clz.getTypeName(), new ArrayList<>());
        findClassMap.get(clz.getTypeName()).add(c);
    }

    /**
     * 获取特定配置属性
     *
     * @since 1.0.1
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
     *
     * @since 1.0.1
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
     *
     * @since 1.0.1
     */
    @Override
    public void addProperty(String k, Object v) {
        this.propertiesMap.put(k, v);
    }

    /**
     * 添加多个属性
     *
     * @since 1.0.1
     */
    @Override
    public void addProperties(Map<String, Object> source) {
        this.propertiesMap.putAll(source);
    }

    /**
     * 清空配置文件
     *
     * @since 1.0.1
     */
    @Override
    public void clearProperties() {
        this.propertiesMap = null;
    }

    /**
     * 重刷配置文件
     *
     * @since 1.0.1
     */
    @Override
    public void refreshProperties() {
        clearProperties();
        CokePropertiesHandler.read();
    }

    /**
     * 增加一个bean
     *
     * @since 1.0.1
     */
    public void addComponent(String name, Object o) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(StrUtil.lowerFirst(name));
        if (null == beanDefinition || beanDefinition.getStep() != 2) {
            return;
        }
        componentMap.put(StrUtil.lowerFirst(name), o);

    }

    /**
     * 增加一个前置生成的bean
     *
     * @param name beanName
     * @param o    bean
     * @since 1.0.1
     */
    public void addPreloadComponent(String name, Object o) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(StrUtil.lowerFirst(name));
        if (null == beanDefinition || beanDefinition.getStep() != 0) {
            return;
        }
        componentMap.put(StrUtil.lowerFirst(name), o);
    }

    /**
     * 是否有相同的bean
     *
     * @param clz bean的class
     * @since 1.0.1
     */
    public boolean hasEqualComponent(Class<?> clz) {
        Collection<Object> values = componentMap.values();
        for (Object value : values) {
            if (value.getClass().getTypeName().equals(clz.getTypeName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 容器本身
     *
     * @since 1.0.1
     */
    private static Container container;

    /**
     * 配置文件流管理
     *
     * @since 1.0.1
     */
    private final Map<String, List<InputStream>> propertiesIsMap = new HashMap<>();

    /**
     * 获取配置文件流管理map
     *
     * @since 1.0.1
     */
    public Map<String, List<InputStream>> getPropertiesIsMap() {
        return propertiesIsMap;
    }

    /**
     * 添加配置文件流
     *
     * @since 1.0.1
     */
    public void addPropertiesIs(String name, InputStream is) {
        if (!propertiesIsMap.containsKey(name)) {
            propertiesIsMap.put(name, new ArrayList<>());
        }
        propertiesIsMap.get(name).add(is);
    }

    /**
     * 获取容器本身
     *
     * @since 1.0.1
     */
    public static Container getContainer() {
        if (null == container) {
            container = new Container();
        }
        return container;
    }

    /**
     * 获取容器上下文
     *
     * @since 1.0.1
     */
    public ApplicationContext applicationContext() {
        return this.getBean(ApplicationContext.class);
    }

    private Container() {

    }

    public List<BeanDefinition> getBeanDefinitions(BeanType beanType) {
        return beanDefinitionMap.values().stream().filter(d -> beanType == d.getBeanType()).collect(Collectors.toList());
    }

    /**
     * 获取bean
     *
     * @since 1.0.1
     */

    @SuppressWarnings("unchecked")
    public <T> T getBean(String name) {
        if (lazyBeanMap.containsKey(name)) {
            return (T) lazyBeanMap.get(StringUtil.getOriginalName(name));
        }
        return (T) componentMap.get(StringUtil.getOriginalName(name));
    }

    /**
     * 判断当前运行时是否支持AOP
     *
     * @since 1.0.1
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
     *
     * @since 1.0.1
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

    /**
     * 获取bean的名称,代理的bean会返回原始名称
     *
     * @since 1.0.1
     */
    public Object getSimpleBean(String name) {
        return componentMap.get(StringUtil.getOriginalName(name));
    }

    /**
     * 获取bean的包装类
     *
     * @since 1.0.1
     */
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

    /**
     * 按类型获取bean
     *
     * @since 1.0.1
     */
    public <T> T getBean(Class<T> clz) {
        List<BeanDefinition> beanDefinitions = getBeanDefinitions(clz);
        if (CollUtil.isNotEmpty(beanDefinitions)) {
            return getBean(beanDefinitions.get(0).getName());
        }
        return null;
    }

    /**
     * 获取指定类型的所有bean
     *
     * @since 1.0.1
     */
    @SuppressWarnings("unchecked")
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

    /**
     * 将BeanDefinition注册到容器中
     *
     * @since 1.0.1
     */
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

    /**
     * 移除容器中的BeanDefinition
     *
     * @since 1.0.1
     */
    @Override
    public void removeBeanDefinition(String name) {
        this.beanDefinitionMap.remove(StringUtil.getOriginalName(name));
    }

    /**
     * 获取容器中的BeanDefinition
     *
     * @since 1.0.1
     */
    @Override
    public BeanDefinition getBeanDefinition(String name) {
        return this.beanDefinitionMap.get(StrUtil.lowerFirst(name));
    }

    /**
     * 获取容器中的代理bean的BeanDefinition
     *
     * @since 1.0.1
     */
    public BeanDefinition getProxyBeanDefinition(String name) {
        return this.beanDefinitionMap.get(StringUtil.getProxyName(name));
    }

    /**
     * 获取bean的代理bean的名称
     *
     * @since 1.0.1
     */
    private String getProxyBeanName(String name) {
        return StringUtil.getProxyName(name);
    }

    /**
     * 是否是代理的bean的 BeanDefinition
     *
     * @since 1.0.1
     */
    public boolean isProxyBeanDefinition(BeanDefinition beanDefinition) {
        return StringUtil.isProxyName(beanDefinition.getName());
    }

    /**
     * 容器中是否存在该代理bean
     *
     * @since 1.0.1
     */
    public boolean containsProxyBean(String name) {
        return this.componentMap.containsKey(getProxyBeanName(name));
    }

    /**
     * 容器中是否存在该代理bean的BeanDefinition
     *
     * @since 1.0.1
     */
    public boolean containsProxyBeanDefinition(String name) {
        return this.beanDefinitionMap.containsKey(getProxyBeanName(name));
    }

    /**
     * 容器中是否存在该类型的代理bean
     *
     * @since 1.0.1
     */
    public boolean containsProxyBean(Class<?> clz) {
        List<BeanDefinition> beanDefinitions = getBeanDefinitions(clz);
        for (BeanDefinition beanDefinition : beanDefinitions) {
            if (beanDefinition.getClass().getTypeName().equals(clz.getTypeName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取指定类型的BeanDefinition
     *
     * @since 1.0.1
     */
    public BeanDefinition getBeanDefinition(Class<?> clz) {
        return getBeanDefinition(clz, false);
    }

    /**
     * 获取指定类型的BeanDefinition
     *
     * @param clz   类型
     * @param proxy 是否是代理bean
     * @since 1.0.1
     */
    public BeanDefinition getBeanDefinition(Class<?> clz, boolean proxy) {
        Collection<BeanDefinition> values = beanDefinitionMap.values();
        for (BeanDefinition value : values) {
            if (StringUtil.isProxyName(value.getName()) && !proxy) {
                continue;
            }
            Class<?> aClass = value.getClz();
            if (clz.isAssignableFrom(aClass)) {
                return value;
            }
        }
        return null;
    }
    /**
     * 获取指定类型的所有BeanDefinition
     *
     * @since 1.0.1
     */
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

    /**
     * 获取所有的BeanDefinition
     *
     * @since 1.0.1
     */
    public Collection<BeanDefinition> getBeanDefinitions() {
        Collection<BeanDefinition> values = beanDefinitionMap.values();
        List<BeanDefinition> beanDefinitionCopyOnWriteArrayList = new ArrayList<>();
        beanDefinitionCopyOnWriteArrayList.addAll(values);
        return beanDefinitionCopyOnWriteArrayList;
    }

    /**
     * 是否存在指定名称的BeanDefinition
     *
     * @since 1.0.1
     */
    @Override
    public boolean containsBeanDefinition(String name) {
        return beanDefinitionMap.containsKey(StrUtil.lowerFirst(name));
    }

    /**
     * 是否存在指定类型的BeanDefinition
     *
     * @since 1.0.1
     */
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

    /**
     * 获取所有的BeanDefinition的名称
     *
     * @since 1.0.1
     */
    @Override
    public String[] getBeanDefinitionNames() {
        String[] strings = new String[beanDefinitionMap.size()];
        return beanDefinitionMap.keySet().toArray(strings);
    }

    /**
     * 获取指定类型bean的BeanDefinition的数量
     *
     * @since 1.0.1
     */
    @Override
    public int getBeanDefinitionCount() {
        return beanDefinitionMap.size();
    }

    /**
     * 获取指定类型bean的数量
     *
     * @since 1.0.1
     */
    @Override
    public int getBeanCount(Class<?> clz) {
        List<?> beans = getBeans(clz);
        return beans.size();
    }

    /**
     * bean名称是都已经被使用
     *
     * @since 1.0.1
     */
    @Override
    public boolean isBeanNameInUse(String name) {
        return beanDefinitionMap.containsKey(StrUtil.lowerFirst(name)) || componentMap.containsKey(StrUtil.lowerFirst(name));
    }

    /**
     * 添加BeanDefinition
     *
     * @since 1.0.1
     */
    public void addBeanDefinition(BeanDefinition beanDefinition) {
        beanDefinition.setName(StrUtil.lowerFirst(beanDefinition.getName()));
        beanDefinitionMap.put(beanDefinition.name, beanDefinition);
    }

    /**
     * 获取代理bean
     *
     * @since 1.0.1
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxyBean(String name) {
        return (T) componentMap.get(getProxyBeanName(name));
    }

    /**
     * 获取代理bean
     *
     * @since 1.0.1
     */
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

    /**
     * 保存事件监听器
     *
     * @since 1.0.2
     */
    public void putListener(ISignalType signalType, Object o) {
        if (!listenerMap.containsKey(signalType.getValue())) {
            listenerMap.put(signalType.getValue(), new ArrayList<>());
        }
        listenerMap.get(signalType.getValue()).add(o);
    }

    /**
     * 保存事件监听器
     *
     * @since 1.0.2
     */
    public void putListener(String signalType, Object o) {
        if (!listenerMap.containsKey(signalType)) {
            listenerMap.put(signalType, new ArrayList<>());
        }
        listenerMap.get(signalType).add(o);
    }

    /**
     * 获取事件监听器
     */
    public List<EventListener> getListener(ISignalType signalType) {
        return listenerMap.get(signalType.getValue()).stream().map(a -> (EventListener) a).collect(Collectors.toList());
    }

    {
        putListener(SignalType.LIFE_CYCLE, new LifeCycleEventListener());   //保存事件监听器 @since 1.0.2
    }

    /**
     * coke生命周期阶段
     *
     * @since 1.0.2
     */
    private CokeCoreLifeCycle cokeCoreLifeCycle = CokeCoreLifeCycle.INIT;

    /**
     * 获取coke核心生命周期
     *
     * @since 1.0.2
     */
    public CokeCoreLifeCycle getCokeCoreLifeCycle() {
        return cokeCoreLifeCycle;
    }

    /**
     * 设置coke核心生命周期
     *
     * @since 1.0.2
     */
    public void setCokeCoreLifeCycle(CokeCoreLifeCycle cokeCoreLifeCycle) {
        this.cokeCoreLifeCycle = cokeCoreLifeCycle;
    }
}
