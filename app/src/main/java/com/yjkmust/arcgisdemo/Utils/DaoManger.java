package com.yjkmust.arcgisdemo.Utils;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.yjkmust.arcgisdemo.gen.DaoMaster;
import com.yjkmust.arcgisdemo.gen.DaoSession;

/**
 * Created by GEOFLY on 2017/8/4.
 */

public class DaoManger extends Application {
    public static DaoManger daoManger;
    public static DaoMaster daoMaster;
    public static DaoSession daoSession;
    @Override
    public void onCreate() {
        super.onCreate();
        if (daoManger == null) {
            daoManger = this;
        }
    }
    public static DaoMaster getDaoMaster(Context context){
        if (daoMaster==null){

            DaoMaster.DevOpenHelper dbtest = new DaoMaster.DevOpenHelper(context, "dbtest", null);
            SQLiteDatabase writableDatabase = dbtest.getWritableDatabase();
            daoMaster = new DaoMaster(writableDatabase);
        }
        return daoMaster;
    }
    public static DaoSession getDaoSession(Context context){
        if (daoSession==null){
            if (daoMaster==null) {
                daoMaster = getDaoMaster(context);
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }
}
