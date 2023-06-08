package pers.warren.ioc.event;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LifeCycleEventListener implements EventListener {
    @Override
    public void consume(Signal signal) {
        if (signal instanceof LifeCycleSignal) {
            LifeCycleSignal ls = (LifeCycleSignal) signal;
            log.info("LifeCycleEventListener {} , {} , {}", ls.getType(), ls.getBeanName(), ls.getStep());
        }

    }
}
