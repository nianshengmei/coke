package pers.warren.ioc.cel;

/**
 * coke表达式异常
 *
 * @author warren
 * @since 1.0.3
 */
public class CokeExpressionException extends RuntimeException {

    public CokeExpressionException(String message) {
        super(message);
    }

    public CokeExpressionException(String message, Throwable cause) {
        super(message, cause);
    }
}
