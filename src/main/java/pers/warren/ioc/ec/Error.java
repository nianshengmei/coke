package pers.warren.ioc.ec;

public interface Error {

    /**
     * 获取错误码
     */
    String getErrorCode();

    /**
     * 获取错误信息
     */
    String getErrorMessage();
}
