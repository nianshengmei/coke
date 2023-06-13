package pers.warren.ioc.event;

import cn.hutool.core.util.StrUtil;
import pers.warren.ioc.annotation.Component;
import pers.warren.ioc.annotation.Listen;
import pers.warren.ioc.annotation.Priority;
import pers.warren.ioc.core.ApplicationContext;
import pers.warren.ioc.core.Container;
import pers.warren.ioc.handler.CokePostHandler;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * 事件监听器后置处理器
 *
 * <p>该处理器负责将容器中所有的EventListener按照监听的事件类型归类</p>
 *
 * @since 1.0.2
 */
@Priority(priority = 9)     //置为高优先
@Component
public class EventCokePostHandler implements CokePostHandler {

    @Resource
    private ApplicationContext applicationContext;

    @Override
    public void run() throws Throwable {
        applicationContext.getBeans(EventListener.class).stream()
                .filter(a -> !a.getClass().getTypeName().equals(LifeCycleEventListener.class.getTypeName()))
                .forEach(eventListener -> {
                    Class<? extends EventListener> aClass = eventListener.getClass();
                    Listen listen = aClass.getAnnotation(Listen.class);
                    String[] value = listen.value();
                    Container container = applicationContext.container();
                    if (StrUtil.isNotEmpty(listen.poolName())) {
                        eventListener.setConsumeEventPoolName(listen.poolName());
                    }
                    Arrays.stream(value).forEach(s -> container.putListener(s, eventListener));
                });
    }
}
