package pers.warren.ioc.event;

/**
 * 事件被处理的类型
 */
public enum SignalProcessorType {

    /**
     * 同步处理
     */
    SYNC("sync"),

    /**
     * 异步处理
     */
    ASYNC("async");

    private final String value;

    SignalProcessorType(String value) {
        this.value = value;
    }

}
