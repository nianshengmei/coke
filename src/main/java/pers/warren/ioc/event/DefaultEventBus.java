package pers.warren.ioc.event;

import lombok.extern.slf4j.Slf4j;
import pers.warren.ioc.core.Container;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * 默认事事件总线的实现
 *
 * @author warren
 */
@Slf4j
public class DefaultEventBus implements EventBus {

    @Resource
    private ExecutorService executorService;

    public DefaultEventBus() {
    }

    @Override
    public void sendSignal(Signal signal) {
        ISignalType signalType = signal.getType();
        String processorType = signalType.getProcessorType();
        switch (processorType) {
            case "sync":
                runListenerSync(signal);
                break;
            case "async":
                runListenerAsync(signal);
        }
    }

    private void runListenerSync(Signal signal) {
        Container.getContainer().getListener(signal.getType()).forEach(eventListener -> {
            CompletableFuture.runAsync(() -> eventListener.consume(signal)).join();
        });
    }

    private void runListenerAsync(Signal signal) {
        Container.getContainer().getListener(signal.getType()).forEach(eventListener -> {
            CompletableFuture.runAsync(() -> eventListener.consume(signal), executorService);
        });
    }
}
