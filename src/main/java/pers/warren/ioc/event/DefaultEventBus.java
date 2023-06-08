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
 * @since 1.0.2
 */
@Slf4j
public class DefaultEventBus implements EventBus {

    /**
     * coke默认的线程池
     *
     * <p>生命周期事件阶段该字段为空,事件走的是new出来的eventBus</p>
     */
    @Resource
    private ExecutorService executorService;

    public DefaultEventBus() {
    }

    /**
     * 发送事件
     *
     * @since 1.0.2
     */
    @Override
    public void sendSignal(Signal signal) {
        ISignalType signalType = signal.getType();
        SignalProcessorType processorType = signalType.getProcessorType();
        switch (processorType) {
            case SYNC:
                runListenerSync(signal);
                break;
            case ASYNC:
                runListenerAsync(signal);
        }
    }

    /**
     * 同步执行事件监听器
     *
     * <p>生命周期事件阶段该字段为空,事件走的是new出来的eventBus，这时候会不使用线程池来处理</p>
     *
     * @since 1.0.2
     */
    private void runListenerSync(Signal signal) {
        if (null == executorService) {
            Container.getContainer().getListener(signal.getType()).forEach(eventListener -> CompletableFuture.runAsync(() -> eventListener.consume(signal)).join());
        } else {
            Container.getContainer().getListener(signal.getType()).forEach(eventListener -> CompletableFuture.runAsync(() -> eventListener.consume(signal), executorService).join());
        }
    }

    /**
     * 异步执行事件监听器
     *
     * <p>该方法必须保证是在运行期执行</p>
     *
     * @since 1.0.2
     */
    private void runListenerAsync(Signal signal) {
        Container.getContainer().getListener(signal.getType()).forEach(eventListener -> CompletableFuture.runAsync(() -> eventListener.consume(signal), executorService));
    }
}
