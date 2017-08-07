package com.yjkmust.arcgisdemo;

import android.app.Application;
import android.content.Context;

/**
 * Created by GEOFLY on 2017/8/7.
 */

public class MyApp extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
    public static Context getInstance(){
        return context;
    }
}
