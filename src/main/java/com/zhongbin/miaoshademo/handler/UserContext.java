package com.zhongbin.miaoshademo.handler;

import com.zhongbin.miaoshademo.pojo.User;

public class UserContext {
    private static ThreadLocal<User> userThreadLocal = new ThreadLocal<>();

    public static void setUser(User user){
        userThreadLocal.set(user);
    }

    public static User getUser(){
        return userThreadLocal.get();
    }
    public static void removeUser(){
        userThreadLocal.remove();
    }
}
