package pers.warren.ioc.event;


/**
 * 抽象的时间监听器
 *
 * @author warren
 * @since 1.0.3
 */
public abstract class AbstractEventListener implements EventListener {

    /**
     * 默认线程池的名称
     */
    private String consumeEventPoolName = "cokeExecutorService";

    @Override
    public String getConsumeEventPoolName() {
        return consumeEventPoolName;
    }

    @Override
    public void setConsumeEventPoolName(String poolName) {
        this.consumeEventPoolName = poolName;
    }
}
