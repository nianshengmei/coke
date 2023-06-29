package pers.warren.ioc.cel;

import pers.warren.ioc.kit.CokeJavaClassFileObject;

public class CELClassLoader extends ClassLoader{

    public Class loadClass(String fullName, CokeJavaClassFileObject javaClassObject) {
        byte[] classData = javaClassObject.getBytes();
        return this.defineClass(fullName, classData, 0, classData.length);
    }
}
