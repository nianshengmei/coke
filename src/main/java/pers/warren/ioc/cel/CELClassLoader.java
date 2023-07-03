package pers.warren.ioc.cel;

import pers.warren.ioc.kit.CokeJavaClassFileObject;

/**
 * CEL类加载器
 *
 * @author warren
 * @since 1.0.3
 */
public class CELClassLoader extends ClassLoader{

    /**
     * 加载类
     *
     * <p>cel使用的一种类加载方式</p>
     *
     * @param fullName 类全名
     * @param javaClassObject 类文件对象
     * @return 类
     */
    public Class loadClass(String fullName, CokeJavaClassFileObject javaClassObject) {
        byte[] classData = javaClassObject.getBytes();
        return this.defineClass(fullName, classData, 0, classData.length);
    }
}
