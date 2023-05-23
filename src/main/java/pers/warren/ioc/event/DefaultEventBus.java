package pers.warren.ioc.event;

import lombok.extern.slf4j.Slf4j;

/**
 * 默认事事件总线的实现
 *
 * @author warren
 */
@Slf4j
public class DefaultEventBus implements EventBus {



    public DefaultEventBus() {
    }

    @Override
    public void sendSignal(Signal signal) {

    }
}
