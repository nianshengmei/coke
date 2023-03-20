package pers.warren.ioc.ec;

public enum WarnEnum implements Error{

        NOT_AOP_ENVIRONMENT("201001","COKE推测应该要注入AOP代理对象，但是不存在AOP运行时环境!"),
        BEAN_WITHOUT_PROXY_INSTANCE("201002","COKE推测应该要注入AOP代理对象，不存在代理实例!"),

    LIFE_CYCLE_EVENTS_MUST_HAVE_NON_PARAMETER_CONSTRUCTORS("201003","生命周期事件必须有无参构造器")
    ;


    private final String errorCode;

    private final String errorMessage;

    WarnEnum(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
