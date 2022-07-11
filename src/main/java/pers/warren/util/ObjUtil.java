package pers.warren.util;

import pers.warren.entity.UserEntity;

public class ObjUtil {

    public static Object getObj(){
        UserEntity user = new UserEntity();
        user.setUserId("S001");
        user.setName("张三");
        user.setSex("难");
        return user;
    }
}
