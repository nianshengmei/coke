package pers.warren.ioc.event;

/**
 * 事件类型接口
 *
 * @since 1.0.2
 */
public interface ISignalType {

    String getValue();

    SignalProcessorType getProcessorType();
}
