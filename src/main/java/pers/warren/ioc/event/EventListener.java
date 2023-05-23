package pers.warren.ioc.event;

/**
 * 事件监听接口
 */
public interface EventListener {

    /**
     * 消费事件
     */
    void consume(Signal signal);
}
