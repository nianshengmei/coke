package pers.warren.ioc.util;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;
import pers.warren.ioc.annotation.Configuration;
import pers.warren.ioc.annotation.Scanner;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 包扫描工具
 *
 * @author warren
 * @since jdk1.8
 */
@UtilityClass
public class ScanUtil {

    /**
     * 一共需要扫描哪些包路径
     */
    private Set<String> scannerPackagePaths = new HashSet<>();


    /**
     * 一共扫描出哪些类
     */
    private final Set<Class<?>> clzs = new LinkedHashSet<>();

    public final Set<String> localFilePath = new HashSet<>();

    public final Set<String> jarFilePath = new HashSet<>();

    public Set<Class<?>> scan() {
        Class<?> mainClass = ReflectUtil.deduceMainApplicationClass(); //从堆栈信息推测主类
        String name = ClassUtil.getPackage(mainClass);
        scanPackageFor("pers.warren.ioc");
        scanPackageFor("org.needcoke");
        scanPackageFor(name);
        scannerPackagePaths = null;
        return clzs;
    }



    /* 扫描包路径 */
    public void scanPackageFor(String packagePath) {
        Set<Class<?>> clzSet = getClzFromPkg(packagePath);
        for (Class<?> aClass : clzSet) {
            Configuration configurationAnnotation = aClass.getAnnotation(Configuration.class);
            if (configurationAnnotation != null) {
                scanArray(configurationAnnotation.scanner());
                Scanner scannerAnnotation = aClass.getAnnotation(Scanner.class);
                if (scannerAnnotation != null) {
                    scanArray(scannerAnnotation.value());
                }
            }
        }
    }

    /* 扫描path数组 */
    public static void scanArray(String[] array) {
        for (String scan : array) {
            scanPackageFor(scan);
            if (scannerPackagePaths == null) {
                scannerPackagePaths = new HashSet<>();
            }
            scannerPackagePaths.add(scan);
        }
    }


    /**
     * 扫描包路径下所有的class文件
     *
     * @param pkg
     * @return
     */
    public Set<Class<?>> getClzFromPkg(String pkg) {
        Set<Class<?>> classes = new LinkedHashSet<>();
        String pkgDirName = pkg.replace('.', '/');
        try {
            Enumeration<URL> urls = ScanUtil.class.getClassLoader().getResources(pkgDirName);

            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {// 如果是以文件的形式保存在服务器上
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");// 获取包的物理路径
                    String reverse = StrUtil.reverse(filePath);
                    String reverse1 = StrUtil.reverse(pkgDirName);
                    String reversePath = reverse.replaceFirst(reverse1, "");
                    String path = StrUtil.reverse(reversePath);
                    localFilePath.add(path);
                    findClassesByFile(pkg, filePath, classes);
                } else if ("jar".equals(protocol)) {// 如果是jar包文件
                    JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
                    findClassesByJar(pkg, jar, classes);
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
     * @param classes 保存包路径下class的集合
     */
    private static void findClassesByFile(String pkgName, String pkgPath, Set<Class<?>> classes) {
        String pkgDirName = pkgName.replace('.', '/');
        String reverse = StrUtil.reverse(pkgPath);
        String reverse1 = StrUtil.reverse(pkgDirName);
        String reversePath = reverse.replaceFirst(reverse1, "");
        String path = StrUtil.reverse(reversePath);
        jarFilePath.add(path);
        File dir = new File(pkgPath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 过滤获取目录，or class文件
        File[] dirfiles = dir.listFiles(pathname -> pathname.isDirectory() || pathname.getName().endsWith("class"));
        if (dirfiles == null || dirfiles.length == 0) {
            return;
        }
        String className;
        Class clz;
        for (File f : dirfiles) {
            if (f.isDirectory()) {
                findClassesByFile(pkgName + "." + f.getName(),
                        pkgPath + "/" + f.getName(),
                        classes);
                continue;
            }
            // 获取类名，干掉 ".class" 后缀
            className = f.getName();
            className = className.substring(0, className.length() - 6);

            // 加载类
            clz = loadClass(pkgName + "." + className);
            if (clz != null && !clzs.contains(clz)) {
                classes.add(clz);
                clzs.add(clz);
            }
        }
    }


    /**
     * 扫描包路径下的所有class文件
     *
     * @param pkgName 包名
     * @param jar     jar文件
     * @param classes 保存包路径下class的集合
     */
    private static void findClassesByJar(String pkgName, JarFile jar, Set<Class<?>> classes) {
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
            claze = loadClass(className.replace("/", "."));
            if (claze != null && !clzs.contains(claze)) {
                classes.add(claze);
                clzs.add(claze);
            }
        }
    }

    private static Class<?> loadClass(String fullClzName) {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(fullClzName);
        } catch (Throwable ignored) {
            if (fullClzName.startsWith(".")) {
                try {
                    fullClzName = fullClzName.replaceFirst(".", "");
                    return Thread.currentThread().getContextClassLoader().loadClass(fullClzName);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("cant't load class with class name :" + fullClzName);
                }
            }
        }
        return null;
    }


}
