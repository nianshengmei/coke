package pers.warren.ioc.core;

public interface Environment {

    String getProperties(String key);

    String addProperties(String key,String value);
}
