package pers.warren.ioc.log;

import pers.warren.ioc.ec.Error;
import org.slf4j.Logger;

/**
 * coke日志
 * @author warren
 */
public interface CokeLogger {

    /**
     * 获取日志实现
     */
    Logger getLogger();

    default void info(Error error) {
        getLogger().info("coke log : code = {} , message = {} .", error.getErrorCode(), error.getErrorMessage());
    }

    default void warn(Error error) {
        getLogger().info("coke warn log : warn code = {} , warn message = {} .", error.getErrorCode(), error.getErrorMessage());
    }

    default void error(Error error) {
        getLogger().info("coke error log : error code = {} , error message = {} .", error.getErrorCode(), error.getErrorMessage());
    }
}
