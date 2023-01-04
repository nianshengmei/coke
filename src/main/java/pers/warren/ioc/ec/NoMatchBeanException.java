package pers.warren.ioc.ec;

/**
 * 没有匹配的bean
 */
public class NoMatchBeanException extends RuntimeException {

    private Error error;

    public NoMatchBeanException(String message) {
        super(message);
    }

    public NoMatchBeanException(Error error) {
        super(error.getErrorCode() + " : " + error.getErrorMessage());
    }
}
