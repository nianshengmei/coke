package pers.warren.ioc.cel;

public class CokeExpressionException extends RuntimeException{

    public CokeExpressionException(String message) {
        super(message);
    }

    public CokeExpressionException(String message, Throwable cause) {
        super(message, cause);
    }
}
