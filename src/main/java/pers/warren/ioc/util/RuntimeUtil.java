package pers.warren.ioc.util;


public class RuntimeUtil {

    /**
     * 是否运行在macOs上
     */
    public static boolean isMacOs() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }


    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    public static boolean isLinux() {
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }
}
