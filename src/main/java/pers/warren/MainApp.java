package pers.warren;

import pers.warren.function.OpFunction;
import pers.warren.util.ObjUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainApp {

    public static void main(String[] args) {
        Object obj = ObjUtil.getObj();

        //1、通过对象获取类
        Class<?> aClass = obj.getClass();
        System.out.println("1、通过对象获取类  "+ aClass);

        //2、获取类的全名
        String fullName = aClass.getName();
        System.out.println("2、获取类的全名  " + fullName);

        //3、获取类的简名
        String simpleName = aClass.getSimpleName();
        System.out.println("3、获取类的简名  " + simpleName);

        //4、查看类拥有的公开属性字段
        {
            System.out.print("4、查看类拥有的公开属性字段    ");
            Field[] declaredFields = aClass.getFields();
            for (Field declaredField : declaredFields) {
                System.out.print(declaredField.getName() + "   ");
            }
            System.out.println();
        }


        //4、查看类拥有的声明的属性字段
        {
            System.out.print("4、查看类拥有的全部属性字段    ");
            Field[] declaredFields = aClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                System.out.print(declaredField.getName() + "   ");
            }
            System.out.println();
        }

        //5、通过class来访问对象的属性值
        try {
            Field nameField = aClass.getDeclaredField("userId");
            Object o = nameField.get(obj);
            System.out.println("5、通过class来访问对象的属性值  " + o);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        //6、访问私有属性
        try {
            Field nameField = aClass.getDeclaredField("name");
            nameField.setAccessible(true);
            Object o = nameField.get(obj);
            System.out.println("6、访问私有属性  " + o);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }


        Class<OpFunction> opFunctionClass = OpFunction.class;

        OpFunction op;

        //7、访问公开成员方法
        {
            System.out.print("7、访问公开成员方法  ");
            Method[] methods = opFunctionClass.getMethods();
            for (Method method : methods) {
                String name = method.getName();
                System.out.print(name+"()    ");
            }
            System.out.println();
        }

        //8、访问声明的成员方法
        {
            System.out.print("8、访问全部成员方法  ");
            Method[] methods = opFunctionClass.getDeclaredMethods();
            for (Method method : methods) {
                String name = method.getName();
                System.out.print(name+"()    ");
            }
            System.out.println();
        }

        // 9、获取构造器并创建对象
        try {
            Constructor<OpFunction> constructor = opFunctionClass.getConstructor();
            op = constructor.newInstance();
            System.out.println("9、获取构造器并创建  "+op);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        // 10、反射执行方法
        try {
            Method paramMethod = opFunctionClass.getDeclaredMethod("paramMethod", String.class);
            paramMethod.setAccessible(true);
            paramMethod.invoke(op ,"10、反射执行方法  hahah");
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
