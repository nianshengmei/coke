package pers.warren.ioc.util;

import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ReflectUtil {


    public String[] getParameterNames(final Method method) {
        List<String> paramNames = new ArrayList<>();
        ClassPool pool = ClassPool.getDefault();
        try {
            CtClass ctClass = pool.getCtClass(method.getDeclaringClass().getName());
            CtMethod ctMethod = ctClass.getDeclaredMethod(method.getName());
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

    public static List<String> getParameterNames(Constructor<?> constructor) {
        List<String> paramNames = new ArrayList<>();
        ClassPool pool = ClassPool.getDefault();
        try {
            CtClass ctClass = pool.getCtClass(constructor.getDeclaringClass().getName());
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

}
