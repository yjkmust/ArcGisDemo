package com.yjkmust.arcgisdemo;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by GEOFLY on 2017/8/7.
 */

public class MyApp extends Application {
    public static Context context;
    public static Toast toast;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
    public static Context getInstance(){
        return context;
    }
    public static void showToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }
}
