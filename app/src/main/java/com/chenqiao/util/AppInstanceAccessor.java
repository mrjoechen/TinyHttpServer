package com.chenqiao.util;

import android.app.Application;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 为了移除本包中对Context对象的直接依赖或传参需要，提供对Application对象的访问点。本类尝试利用反射获取当前Application实例。如果实际使用时报错，可以在主程序的Application的onCreate中手动设置.
 */

public class AppInstanceAccessor {
    private static final String TAG = "AppInstanceAccessor";
    private static Application sApp;

    static {
        try {
            Class clazz = Class.forName("android.app.AppGlobals");
            Method method = clazz.getDeclaredMethod("getInitialApplication");
            sApp = (Application) method.invoke(null);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            Log.e(TAG, "obtain application instance by reflection failed, please call setApp to set one in onCreate of Application class");
        }
    }
    
    static Application getApp() {
        return sApp;
    } 

    public static void setApp(Application app) {
        if (sApp == null) {
            sApp = app;
        }
    }
}
