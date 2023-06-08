package pers.warren.ioc.event;


/**
 * 事件总线标准事件接口
 *
 * @author warren
 * @since 1.0.2
 */

public interface Signal {

    /**
     * 获取事件类型
     */
    /**
     * @since 1.0.2
     */
    ISignalType getType();

    /**
     * 获取事件处理类型
     *
     * @since 1.0.2
     */
    SignalProcessorType getProcessorType();


}
