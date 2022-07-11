package pers.warren.ioc;

import pers.warren.ioc.util.ScanUtil;

import java.util.Set;

public class IocApplication {

    private static Set<Class<?>> clzSet;

    public static void run(Class<?> clz , String[] args) {
        Class<?> mainClass = deduceMainApplicationClass(); //从堆栈信息推测主类
        clzSet = ScanUtil.scan(mainClass);   //扫描类




    }


    /* 从堆栈信息推测主类 */
    private static Class<?> deduceMainApplicationClass() {
        try {
            StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                if ("main".equals(stackTraceElement.getMethodName())) {
                    return Class.forName(stackTraceElement.getClassName());
                }
            }
        }
        catch (ClassNotFoundException ex) {
            // Swallow and continue
        }
        return null;
    }
}
