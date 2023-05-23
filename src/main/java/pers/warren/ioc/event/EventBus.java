package pers.warren.ioc.event;

/**
 * 事件总线
 */
public interface EventBus {

    /**
     * 事件总线发送事件
     */
    void sendSignal(Signal signal);

}
