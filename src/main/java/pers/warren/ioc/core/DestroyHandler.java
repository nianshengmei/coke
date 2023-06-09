package pers.warren.ioc.core;

/**
 * coke销毁处理器
 *
 * @author warren
 * @since 1.0.2
 */
public abstract class DestroyHandler implements Runnable{

    /**
     * 销毁
     * <p>该方法会在coke容器被销毁前调用</p>
     *
     * @since 1.0.2
     */
   public abstract void destroy();


    @Override
    public void run() {
        destroy();
    }
}
