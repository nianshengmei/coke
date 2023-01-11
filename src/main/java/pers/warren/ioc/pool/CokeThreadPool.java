package pers.warren.ioc.pool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import pers.warren.ioc.annotation.Init;
import pers.warren.ioc.annotation.Value;

import java.util.concurrent.*;

public class CokeThreadPool {

    public CokeThreadPool() {
        namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("coke-pool-%d").build();
    }

    @Init
    public void init(){
        service = new ThreadPoolExecutor(
                iocCoreThreadPoolSize,
                iocMaximumPoolSize,
                iocKeepAliveTime,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(iocPoolCapacity),
                namedThreadFactory,
                new ThreadPoolExecutor.AbortPolicy()
        );

    }


    /**
     * 自定义线程名称,方便的出错的时候溯源
     */
    private ThreadFactory namedThreadFactory ;

    @Value("coke.ioc.pool.coreSize:4")
    private int iocCoreThreadPoolSize = 4;

    @Value("coke.ioc.pool.maxSize:16")
    private int iocMaximumPoolSize;

    @Value("coke.ioc.pool.keepAliveTime:2000")
    private long iocKeepAliveTime;

    @Value("coke.ioc.pool.iocPoolCapacity:99999")
    private int iocPoolCapacity;


    /**
     * corePoolSize    线程池核心池的大小
     * maximumPoolSize 线程池中允许的最大线程数量
     * keepAliveTime   当线程数大于核心时，此为终止前多余的空闲线程等待新任务的最长时间
     * unit            keepAliveTime 的时间单位
     * workQueue       用来储存等待执行任务的队列
     * threadFactory   创建线程的工厂类
     * handler         拒绝策略类,当线程池数量达到上线并且workQueue队列长度达到上限时就需要对到来的任务做拒绝处理
     */
    private  ExecutorService service;
    /**
     * 获取线程池
     * @return 线程池
     */
    public ExecutorService getExecutorService() {
        return service;
    }

    /**
     * 使用线程池创建线程并异步执行任务
     * @param r 任务
     */
    public void newTask(Runnable r) {
        service.execute(r);
    }

}
