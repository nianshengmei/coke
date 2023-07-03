package pers.warren.ioc.core;

import javax.annotation.Resource;

/**
 * coke销毁处理器
 *
 * @author warren
 * @since 1.0.2
 */
public abstract class DestroyHandler extends Thread{

    @Resource
    protected ApplicationContext applicationContext;

    /**
     * 销毁
     * <p>该方法会在coke容器被销毁前调用</p>
     *
     * @since 1.0.3
     */
   public abstract void destroyRun();

    /**
     * 执行容器的执行方法
     *
     * @since 1.0.3
     */
    @Override
    public void run() {
        applicationContext.container().setCokeCoreLifeCycle(CokeCoreLifeCycle.DESTROY);
        destroyRun();
    }
}
