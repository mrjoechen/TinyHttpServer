package com.chenqiao;

import android.app.Application;

import com.chenqiao.server.TinyHttpd;

/**
 * Created by chenqiao on 2018/7/24.
 */

public class App extends Application {


    private static App Instance;

    @Override
    public void onCreate() {
        super.onCreate();
        Instance = this;
    }

    public static App getInstance(){
        return Instance;
    }
}
