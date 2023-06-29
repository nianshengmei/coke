package pers.warren.ioc.cel;

public interface Expression {

    <T> T getValue(Class<T> clazz);
}
