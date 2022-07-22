package pers.warren.ioc.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import pers.warren.ioc.core.ValueField;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class InjectUtil {


    public static Object getDstValue(ValueField field) {
        Object v = null;
        if (null != field.getConfigValue()) {
            v = field.getConfigValue();
        } else {
            v = field.getDefaultValue();
        }
        String vs = String.valueOf(v);
        if (field.getType().getTypeName().equals(Integer.class.getTypeName())) {
            return Integer.parseInt(vs);
        } else if (field.getType().getTypeName().equals(Double.class.getTypeName())) {
            return Double.parseDouble(vs);
        } else if (field.getType().getTypeName().equals(Long.class.getTypeName()) ||
                field.getType().getTypeName().equals(long.class.getTypeName())) {
            return Long.parseLong(vs);
        } else if (field.getType().getTypeName().equals(Boolean.class.getTypeName())) {
            return Boolean.parseBoolean(vs);
        } else if (field.getType().getTypeName().equals(Short.class.getTypeName())) {
            return Short.parseShort(vs);
        } else if (field.getType().getTypeName().equals(Byte.class.getTypeName())) {
            return Byte.parseByte(vs);
        } else if (field.getType().getTypeName().equals(Character.class.getTypeName())) {
            return vs;
        } else if (field.getType().getTypeName().equals(String.class.getTypeName())) {
            return vs;
        } else if (field.getType().getTypeName().equals("int")) {
            return Integer.parseInt(vs);
        } else if (field.getType().getTypeName().equals("double")) {
            return Double.parseDouble(vs);
        } else if (field.getType().getTypeName().equals("boolean")) {
            return Boolean.parseBoolean(vs);
        } else if (field.getType().getTypeName().equals(ArrayList.class.getTypeName())) {
            List list = new ArrayList<>();
            fun(field, list);
            return list;
        } else if (field.getType().getTypeName().equals(LinkedList.class.getTypeName())) {
            List list = new LinkedList();
            fun(field, list);
            return list;
        } else if (field.getType().getTypeName().equals(List.class.getTypeName())) {
            List list = new ArrayList<>();
            fun(field, list);
            return list;
        } else if (field.getType().getTypeName().equals(String[].class.getTypeName())) {
            Object[] vArr = (Object[]) field.getConfigValue();
            String[] ss = new String[vArr.length];
            for (int i = 0; i < ss.length; i++) {
                ss[i] = String.valueOf(vArr[i]);
            }
            return ss;
        } else if (field.getType().getTypeName().equals(Integer[].class.getTypeName())) {
            Object[] vArr = (Object[]) field.getConfigValue();
            Integer[] ns = new Integer[vArr.length];
            for (int i = 0; i < ns.length; i++) {
                ns[i] = Integer.parseInt(String.valueOf(vArr[i]));
            }
            return ns;
        } else if (field.getType().getTypeName().equals(Double[].class.getTypeName())) {
            Object[] vArr = (Object[]) field.getConfigValue();
            Double[] ns = new Double[vArr.length];
            for (int i = 0; i < ns.length; i++) {
                ns[i] = Double.parseDouble(String.valueOf(vArr[i]));
            }
            return ns;
        } else if (field.getType().getTypeName().equals(Long[].class.getTypeName())) {
            Object[] vArr = (Object[]) field.getConfigValue();
            Long[] ns = new Long[vArr.length];
            for (int i = 0; i < ns.length; i++) {
                ns[i] = Long.parseLong(String.valueOf(vArr[i]));
            }
            return ns;
        } else if (field.getType().getTypeName().equals(int[].class.getTypeName())) {
            Object[] vArr = (Object[]) field.getConfigValue();
            int[] ns = new int[vArr.length];
            for (int i = 0; i < ns.length; i++) {
                ns[i] = Integer.parseInt(String.valueOf(vArr[i]));
            }
            return ns;
        } else if (field.getType().getTypeName().equals(double[].class.getTypeName())) {
            Object[] vArr = (Object[]) field.getConfigValue();
            double[] ns = new double[vArr.length];
            for (int i = 0; i < ns.length; i++) {
                ns[i] = Double.parseDouble(String.valueOf(vArr[i]));
            }
            return ns;
        } else if (field.getType().getTypeName().equals(boolean[].class.getTypeName())) {
            Object[] vArr = (Object[]) field.getConfigValue();
            boolean[] ns = new boolean[vArr.length];
            for (int i = 0; i < ns.length; i++) {
                ns[i] = Boolean.parseBoolean(String.valueOf(vArr[i]));
            }
            return ns;
        } else if (field.getType().getTypeName().equals(Boolean.class.getTypeName())) {
            Object[] vArr = (Object[]) field.getConfigValue();
            Boolean[] ns = new Boolean[vArr.length];
            for (int i = 0; i < ns.length; i++) {
                ns[i] = Boolean.parseBoolean(String.valueOf(vArr[i]));
            }
            return ns;
        } else if (field.getType().getTypeName().equals(char[].class.getTypeName())) {
            Object[] vArr = (Object[]) field.getConfigValue();
            char[] ns = new char[vArr.length];
            for (int i = 0; i < ns.length; i++) {
                ns[i] = (char) vArr[i];
            }
            return ns;
        } else if (field.getType().getTypeName().equals(Character.class.getTypeName())) {
            Object[] vArr = (Object[]) field.getConfigValue();
            Character[] ns = new Character[vArr.length];
            for (int i = 0; i < ns.length; i++) {
                ns[i] = (char) vArr[i];
            }
            return ns;
        } else if (field.getType().getClass().isAssignableFrom(Date.class)) {
            Object[] vArr = (Object[]) field.getConfigValue();
            Date[] ns = new Date[vArr.length];
            for (int i = 0; i < ns.length; i++) {
                ns[i] = DateUtil.parse(String.valueOf(vArr[i]), DatePattern.NORM_DATETIME_PATTERN);
            }
            return ns;
        } else {
            throw new RuntimeException("不支持该类型转换，请使用String转换");
        }
    }


    public static void fun(ValueField field, List list) {
        ParameterizedType genericType = (ParameterizedType) field.getGenericType();
        Object[] vArr = (Object[]) field.getConfigValue();
        for (Object o : vArr) {
            ValueField field1 = new ValueField();
            field1.setConfigValue(o);
            String typeName = genericType.getActualTypeArguments()[0].getTypeName();
            Class<?> aClass = null;
            try {
                aClass = Class.forName(typeName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            field1.setType(aClass);
            list.add(getDstValue(field1));
        }
    }

}
