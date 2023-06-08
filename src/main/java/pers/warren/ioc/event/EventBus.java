package pers.warren.ioc.event;

/**
 * 事件总线
 *
 * @since 1.0.2
 */
public interface EventBus {

    /**
     * 事件总线发送事件
     *
     * @since 1.0.2
     */
    void sendSignal(Signal signal);

}
