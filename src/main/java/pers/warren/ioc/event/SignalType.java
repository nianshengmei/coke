package pers.warren.ioc.event;

import lombok.Getter;

public enum SignalType implements ISignalType {

    LIFE_CYCLE("life_cycle", "sync"), //生命周期事件
    REMOTE("remote","async")
    ;

    @Getter
    private final String value;

    @Getter
    private final String processorType;  // 处理器类型

    SignalType(String value, String processorType) {
        this.value = value;
        this.processorType = processorType;
    }
}
