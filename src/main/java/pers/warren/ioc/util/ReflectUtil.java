package pers.warren.ioc.util;

import javassist.*;
import javassist.bytecode.*;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ReflectUtil {


    public String[] getParameterNames(final Method method) {
        if (Modifier.isInterface(method.getDeclaringClass().getModifiers())) {
            return getInterfaceParamterNames(method);
        }
        List<String> paramNames = new ArrayList<>();
        ClassPool pool = ClassPool.getDefault();
        try {
            CtClass ctClass = pool.getCtClass(method.getDeclaringClass().getName());
            CtClass[] ctClasses = changeArray(method.getParameterTypes());
            CtMethod ctMethod = ctClass.getDeclaredMethod(method.getName(), ctClasses);
            // 使用javassist的反射方法的参数名
            javassist.bytecode.MethodInfo methodInfo = ctMethod.getMethodInfo();
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
            if (attr != null) {
                int len = ctMethod.getParameterTypes().length;
                // 非静态的成员函数的第一个参数是this
                int pos = Modifier.isStatic(ctMethod.getModifiers()) ? 0 : 1;
                for (int i = 0; i < len; i++) {
                    paramNames.add(attr.variableName(i + pos));
                }

            }
            String[] arr = new String[paramNames.size()];
            return paramNames.toArray(arr);
        } catch (NotFoundException e) {
            String msg = method.getDeclaringClass().getName() + "#" + method.getName();
            throw new RuntimeException("get method param error method = " + msg, e);
        }
    }

    public static String[] getInterfaceParamterNames(final Method method) {
        ClassPool pool = ClassPool.getDefault();
        try {
            CtClass ctClass = pool.get(method.getDeclaringClass().getName());
            CtMethod ctMethod = ctClass.getDeclaredMethod(method.getName());
            // 使用javassist的反射方法的参数名
            MethodInfo methodInfo = ctMethod.getMethodInfo();

            MethodParametersAttribute methodParameters = (MethodParametersAttribute) methodInfo.getAttribute("MethodParameters");
            //获取参数的个数
            int count = ctMethod.getParameterTypes().length;

            CtClass[] pCtClass = ctMethod.getParameterTypes();
            String[] rets = new String[pCtClass.length];
            for (int i = 0; i < count; i++) {
                String str = methodParameters.getConstPool().getUtf8Info(ByteArray.readU16bit(methodParameters.get(), i * 4 + 1));
                rets[i] = str;
            }

            return rets;
        } catch (NotFoundException e) {
            String msg = method.getDeclaringClass().getName() + "#" + method.getName();
            throw new RuntimeException("get method param error method = " + msg, e);
        }
    }

    public static List<String> getParameterNames(Constructor<?> constructor) {
        List<String> paramNames = new ArrayList<>();
        ClassPool pool = ClassPool.getDefault();
        try {
            Class<?> declaringClass = constructor.getDeclaringClass();
            CtClass ctClass = pool.getCtClass(declaringClass.getName());
            CtConstructor[] constructors = ctClass.getConstructors();
            Class<?>[] pts = constructor.getParameterTypes();
            CtConstructor ctc = null;
            for (CtConstructor ctConstructor : constructors) {
                if (ctConstructor.isConstructor() && ctConstructor.getModifiers() == constructor.getModifiers()) {
                    CtClass[] parameterTypes = ctConstructor.getParameterTypes();
                    if (null != parameterTypes && pts.length == parameterTypes.length) {
                        boolean match = true;
                        for (int i = 0; i < parameterTypes.length; i++) {
                            if (!parameterTypes[i].getName().equals(pts[i].getName())) {
                                match = false;
                            }
                        }
                        if (match) {
                            ctc = ctConstructor;
                            break;
                        }
                    }
                }
            }
            // 使用javassist的反射方法的参数名
            javassist.bytecode.MethodInfo methodInfo = ctc.getMethodInfo();
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
            if (attr != null) {
                int len = ctc.getParameterTypes().length;
                // 非静态的成员函数的第一个参数是this
                int pos = Modifier.isStatic(ctc.getModifiers()) ? 0 : 1;
                for (int i = 0; i < len; i++) {
                    paramNames.add(attr.variableName(i + pos));
                }

            }

            return paramNames;
        } catch (NotFoundException e) {
            String msg = constructor.getDeclaringClass().getName() + "#" + constructor.getName();
            throw new RuntimeException("get constructor param error method = " + msg, e);
        }
    }

    public boolean containsAnnotation(Method method, Class annotationClz) {
        if (null != method.getAnnotation(annotationClz)) {
            return true;
        }
        return false;
    }

    public boolean containsAnnotation(Class<?> clz, Class annotationClz) {
        if (null != clz.getAnnotation(annotationClz)) {
            return true;
        }
        return false;
    }

    private CtClass[] changeArray(Class[] array) throws NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        CtClass[] clzArr = new CtClass[array.length];
        for (int i = 0; i < array.length; i++) {
            clzArr[i] = pool.get(array[i].getName());
        }
        return clzArr;
    }

    public Class<?> mainClz;

    /* 从堆栈信息推测主类 */
    public static Class<?> deduceMainApplicationClass() {
        if (null != mainClz) {
            return mainClz;
        }
        try {
            StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                if ("main".equals(stackTraceElement.getMethodName())) {
                    mainClz = Class.forName(stackTraceElement.getClassName());
                    return mainClz;
                }
            }
        } catch (ClassNotFoundException ex) {
            // Swallow and continue
        }
        return null;
    }

}
