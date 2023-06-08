package pers.warren.ioc.event;

/**
 * 事件被处理的类型
 */
public enum SignalProcessorType {


    SYNC("sync"), ASYNC("async");

    private final String value;

    SignalProcessorType(String value) {
        this.value = value;
    }

}
