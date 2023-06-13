package pers.warren.ioc.event;

/**
 * 事件监听接口
 *
 * @author warren
 * @since 1.0.3
 */
public interface EventListener {

    /**
     * 消费事件
     *
     * @since 1.0.3
     */
    void consume(Signal signal);

    /**
     * 消费事件的线程池名称
     *
     * @since 1.0.3
     */
    default String getConsumeEventPoolName() {
        return "cokeExecutorService";
    }

    /**
     * 设置消费线程池的名称
     *
     * @since 1.0.3
     */
    default void setConsumeEventPoolName(String poolName) {
    }
}
