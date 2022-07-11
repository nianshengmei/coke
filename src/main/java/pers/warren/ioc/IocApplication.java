package pers.warren.ioc;

import cn.hutool.core.io.resource.ResourceUtil;
import lombok.extern.slf4j.Slf4j;
import pers.warren.ioc.util.ScanUtil;

import java.util.Set;

/**
 * 容器的启动辅助类
 */

@Slf4j
public class IocApplication {

    /**
     * 所有符合扫描规则下的类的集合
     */
    private static Set<Class<?>> clzSet;

    private static long startTimeMills;

    public static void run(Class<?> clz, String[] args) {
        start();
        clzSet = ScanUtil.scan();   //扫描类

        end();
    }

    private static void start() {
        startTimeMills = System.currentTimeMillis();
        printBanner();
    }

    private static void printBanner() {
        System.out.println(ResourceUtil.readUtf8Str("banner.txt"));
    }


    private static void end() {
        log.info("needcoke-ioc start ok! cost = {}ms", System.currentTimeMillis() - startTimeMills);
    }

}
