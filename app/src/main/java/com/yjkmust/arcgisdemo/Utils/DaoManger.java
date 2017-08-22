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
    private static DaoMaster.DevOpenHelper dbtests;


    @Override
    public void onCreate() {
        super.onCreate();
        if (daoManger == null) {
            daoManger = this;
        }
    }
    public static DaoMaster getDaoMaster(Context context){
        if (daoMaster==null){
            /**
             * 指定数据库文件生成目录,重写ContextWrapper类
             */
            DataBaseContext mContext = new DataBaseContext(context);
            dbtests = new DaoMaster.DevOpenHelper(mContext, "demo.db", null);
            SQLiteDatabase normal = dbtests.getWritableDatabase();//数据库不加密
            daoMaster = new DaoMaster(normal);
//            Database encryption = dbtests.getEncryptedWritableDb("encryption");//数据库加密
//            daoMaster = new DaoMaster(encryption);

            /**
             * 默认数据库文件生成目录
             */
//            DaoMaster.DevOpenHelper dbtest = new DaoMaster.DevOpenHelper(context, "dbtest", null);
//            Database oooooh = dbtest.getEncryptedWritableDb("oooooh");
//            daoMaster = new DaoMaster(oooooh);
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
