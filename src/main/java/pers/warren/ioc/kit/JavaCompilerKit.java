package pers.warren.ioc.kit;

import pers.warren.ioc.cel.CokeExpressionException;
import pers.warren.ioc.core.BeanDefinition;
import pers.warren.ioc.core.Container;

/**
 * java编译工具类
 * 
 * @since 1.0.3
 */
public class JavaCompilerKit {
    
    /**
     * 动态编译器
     * 
     * @since 1.0.3
     */
    private final static DynamicCompiler dynamicCompiler = new DynamicCompiler();
    
    /**
     * 传入source生成结果
     * <p>指定类名生成持久类</p>
     * @param javaSourceCode return代码
     * @param className 类名
     * @since 1.0.3
     */
    public static Object compile(String javaSourceCode, String className) {
        try {
            String celTemplate = Container.getContainer().getCelTemplate();
            celTemplate = celTemplate.replace("#CN#", className).replace("#CODE#", javaSourceCode);
            Class<?> aClass = dynamicCompiler.compileToClass("pers.warren.ioc.cel.proxy." + className, celTemplate);
            Object invoke = aClass.getMethod("runCode").invoke(aClass.newInstance());
            return invoke;
        } catch (Throwable e) {
            throw new CokeExpressionException(e.getMessage());
        }
    }

    /**
     * 传入source生成结果
     * @param javaSourceCode return代码
     * @since 1.0.3
     */
    public static Object compile(String javaSourceCode){
        return compile(javaSourceCode, "CELClz");
    }
    
    /**
     * 可操作bean的编译器
     *
     * @since 1.0.3
     */
    public static Object runBeanString(String javaSourceCode,String beanName, String className){
        try {
            BeanDefinition beanDefinition = Container.getContainer().getBeanDefinition(beanName);
            String celTemplate = Container.getContainer().getCelBeanTemplate();
            celTemplate = celTemplate.replace("#CN#", className)
                    .replace("#CODE#", javaSourceCode)
                    .replace("#BN#", beanName)
                    .replace("#BT#", beanDefinition.getClz().getName())
                    .replace("#PKGN#", beanDefinition.getClz().getPackage().getName());
            Class<?> aClass = dynamicCompiler.compileToClass("pers.warren.ioc.cel.proxy." + className, celTemplate);
            Object invoke = aClass.getMethod("runCode").invoke(aClass.newInstance());
            return invoke;
        } catch (Throwable e) {
            if (!Container.getContainer().containsBeanDefinition(beanName)) {
                throw new CokeExpressionException("Bean " + beanName + " not found");
            }
            throw new CokeExpressionException(e.getMessage());
        }
    }

    /**
     * 可操作bean的编译器
     *
     * @since 1.0.3
     */
    public static Object runBeanString(String javaSourceCode,String beanName){
        return runBeanString(javaSourceCode, beanName, "BCELClz");
    }
}
