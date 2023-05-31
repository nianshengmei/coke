package pers.warren.ioc.event;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LifeCycleEventListener implements EventListener{
    @Override
    public void consume(Signal signal) {
        log.info("LifeCycleEventListener {}",signal);
    }
}
