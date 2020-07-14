package com.a1anwang.ai_master;

import android.app.Application;

/**
 * Created by a1anwang.com on 2019/8/8.
 */
public class MyApp extends Application {
    private static MyApp instance;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        NavigationManager.getInstance().init(this);

    }
    public static MyApp getInstance() {
        return instance;
    }


}
