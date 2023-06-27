package pers.warren.ioc.util;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassUtils {

    private static ClassLoader[] getClassLoaders(ClassLoader classLoader) {
        return new ClassLoader[]{
                classLoader,
                Thread.currentThread().getContextClassLoader(),
                ClassUtils.class.getClassLoader()
                };
    }

    /**
     * @param name
     * @param classLoader
     * @return
     * @since 3.4.3
     */
    public static Class<?> toClassConfident(String name, ClassLoader classLoader) {
        try {
            return loadClass(name, getClassLoaders(classLoader));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("找不到指定的class！请仅在明确确定会有 class 的时候，调用该方法", e);
        }
    }


    private static Class<?> loadClass(String className, ClassLoader[] classLoaders) throws ClassNotFoundException {
        for (ClassLoader classLoader : classLoaders) {
            if (classLoader != null) {
                try {
                    return Class.forName(className, true, classLoader);
                } catch (ClassNotFoundException e) {
                    // ignore
                }
            }
        }
        throw new ClassNotFoundException("Cannot find class: " + className);
    }

    /**
     * <p>
     * 获取当前对象的 class
     * </p>
     *
     * @param clazz 传入
     * @return 如果是代理的class，返回父 class，否则返回自身
     */
    public static Class<?> getUserClass(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return isProxy(clazz) ? clazz.getSuperclass() : clazz;
    }

    /**
     * 代理 class 的名称
     */
    private static final List<String> PROXY_CLASS_NAMES = Arrays.asList("com.sun.proxy");

    /**
     * 判断是否为代理对象
     *
     * @param clazz 传入 class 对象
     * @return 如果对象class是代理 class，返回 true
     */
    public static boolean isProxy(Class<?> clazz) {
        if (clazz != null) {
            for (Class<?> cls : clazz.getInterfaces()) {
                for (String proxyClassName : PROXY_CLASS_NAMES) {
                    if(cls.getName().startsWith(proxyClassName)){
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * 扫描包路径下所有的class文件
     *
     * @param pkg
     * @return
     */
    public static Set<Class<?>> getClzFromPkg(String pkg) {
        Set<Class<?>> classes = new LinkedHashSet<>();
        String pkgDirName = pkg.replace('.', '/');
        try {
            Enumeration<URL> urls = ClassUtils.class.getClassLoader().getResources(pkgDirName);

            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {// 如果是以文件的形式保存在服务器上
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");// 获取包的物理路径
                    classes.addAll(findClassesByFile(pkg, filePath));
                } else if ("jar".equals(protocol)) {// 如果是jar包文件
                    JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
                    classes.addAll(findClassesByJar(pkg, jar));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }


    /**
     * 扫描包路径下的所有class文件
     *
     * @param pkgName 包名
     * @param pkgPath 包对应的绝对地址
     */
    private static List<Class<?>> findClassesByFile(String pkgName, String pkgPath) {
        List<Class<?>> ret = new ArrayList<>();
        String pkgDirName = pkgName.replace('.', '/');
        String reverse = StrUtil.reverse(pkgPath);
        String reverse1 = StrUtil.reverse(pkgDirName);
        String reversePath = reverse.replaceFirst(reverse1, "");
        String path = StrUtil.reverse(reversePath);
        File dir = new File(pkgPath);
        if (!dir.exists() || !dir.isDirectory()) {
            return Collections.emptyList();
        }
        // 过滤获取目录，or class文件
        File[] dirfiles = dir.listFiles(pathname -> pathname.isDirectory() || pathname.getName().endsWith("class"));
        if (dirfiles == null || dirfiles.length == 0) {
            return Collections.emptyList();
        }
        String className;
        Class clz;
        for (File f : dirfiles) {
            if (f.isDirectory()) {
                continue;
            }
            // 获取类名，干掉 ".class" 后缀
            className = f.getName();
            className = className.substring(0, className.length() - 6);

            // 加载类
            clz = ClassUtil.loadClass(pkgName + "." + className);
            ret.add(clz);
        }
        return ret;
    }


    /**
     * 扫描包路径下的所有class文件
     *
     * @param pkgName 包名
     * @param jar     jar文件
     */
    private static List<Class<?>> findClassesByJar(String pkgName, JarFile jar) {
        List<Class<?>> ret = new ArrayList<>();
        String pkgDir = pkgName.replace(".", "/");
        Enumeration<JarEntry> entry = jar.entries();

        JarEntry jarEntry;
        String name, className;
        Class<?> claze;
        while (entry.hasMoreElements()) {
            jarEntry = entry.nextElement();

            name = jarEntry.getName();
            if (name.charAt(0) == '/') {
                name = name.substring(1);
            }
            if (jarEntry.isDirectory() || !name.startsWith(pkgDir) || !name.endsWith(".class")) {
                // 非指定包路径， 非class文件
                continue;
            }
            // 去掉后面的".class", 将路径转为package格式
            className = name.substring(0, name.length() - 6);
            claze = ClassUtil.loadClass(className.replace("/", "."));
            if (claze != null) {
                ret.add(claze);
            }
        }
        return ret;
    }



}
