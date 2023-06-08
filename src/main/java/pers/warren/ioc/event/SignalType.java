package pers.warren.ioc.event;

import lombok.Getter;

/**
 * coke所默认支持的事件类型
 *
 * @author warren
 * @since 1.0.2
 */
public enum SignalType implements ISignalType {

    LIFE_CYCLE("life_cycle", SignalProcessorType.SYNC), //生命周期事件
    REMOTE("remote", SignalProcessorType.ASYNC);

    @Getter
    private final String value;

    @Getter
    private final SignalProcessorType processorType;  // 处理器类型

    SignalType(String value, SignalProcessorType processorType) {
        this.value = value;
        this.processorType = processorType;
    }

}
