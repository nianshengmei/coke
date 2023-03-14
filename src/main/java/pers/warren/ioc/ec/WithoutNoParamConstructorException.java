package pers.warren.ioc.ec;

/**
 * 没有无参构造器的异常
 *
 * @author warren
 */
public class WithoutNoParamConstructorException extends RuntimeException {

    private Error error;

    public WithoutNoParamConstructorException(String message) {
        super(message);
    }

    public WithoutNoParamConstructorException(Error error) {
        super(error.getErrorCode() + " : " + error.getErrorMessage());
    }
}
