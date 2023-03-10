package pers.warren.ioc.core;

import pers.warren.ioc.core.bean.BeanWrapper;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractContainer implements Container{

    public Map<String, BeanWrapper>  beanMap = new HashMap<>();

    public Map<String, Type> beanTypeMap = new HashMap<>();


}
