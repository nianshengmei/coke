package pers.warren.ioc.pool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Data;
import pers.warren.ioc.annotation.*;
import pers.warren.ioc.condition.ConditionalOnMissingBean;

import javax.annotation.Resource;
import java.util.concurrent.*;

@Data
@Component
public class CokeThreadPool {

    public CokeThreadPool() {
        namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("coke-pool-%d").build();
    }


    /**
     * 自定义线程名称,方便的出错的时候溯源
     */
    private ThreadFactory namedThreadFactory ;

    @Value("coke.ioc.pool.coreSize:4")
    private int iocCoreThreadPoolSize;

    @Value("coke.ioc.pool.maxSize:16")
    private int iocMaximumPoolSize;

    @Value("coke.ioc.pool.keepAliveTime:2000")
    private long iocKeepAliveTime;

    @Value("coke.ioc.pool.iocPoolCapacity:0")
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
    @Resource
    private  ExecutorService service;

    /**
     * 获取线程池
     * @return 线程池
     */
    @Bean
    @After(name = "cokeThreadPool")
    @ConditionalOnMissingBean(name = "cokeExecutorService")
    public ExecutorService cokeExecutorService() {
        ExecutorService executorService = null ;
        if (iocPoolCapacity < 100) {
            executorService = new ThreadPoolExecutor(
                    iocCoreThreadPoolSize,
                    iocMaximumPoolSize,
                    iocKeepAliveTime,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingDeque<>(),
                    namedThreadFactory,
                    new ThreadPoolExecutor.AbortPolicy()
            );
        } else {
            executorService = new ThreadPoolExecutor(
                    iocCoreThreadPoolSize,
                    iocMaximumPoolSize,
                    iocKeepAliveTime,
                    TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(iocPoolCapacity),
                    namedThreadFactory,
                    new ThreadPoolExecutor.AbortPolicy()
            );
        }
        return executorService;
    }

    /**
     * 使用线程池创建线程并异步执行任务
     * @param r 任务
     */
    public void newTask(Runnable r) {
        service.execute(r);
    }

}
