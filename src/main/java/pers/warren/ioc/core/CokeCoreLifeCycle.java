package pers.warren.ioc.core;

import lombok.Getter;

/**
 * coke容器的生命周
 *
 * @since 1.0.2
 */
@Getter
public enum CokeCoreLifeCycle {

    INIT("init", "初始化"),

    POST_RUN("postRun", "后置运行"),

    RUNNING("running", "运行中"),

    PRE_DESTROY("preDestroy", "销毁前"),

    DESTROY("destroy", "销毁");

    /**
     * 生命周期阶段
     */
    private String step;

    /**
     * 描述
     */
    private String desc;

    CokeCoreLifeCycle(String step, String desc) {
        this.step = step;
        this.desc = desc;
    }
}
