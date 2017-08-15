package com.yjkmust.arcgisdemo.Utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by GEOFLY on 2017/8/15.
 * 指定GreenDao数据库文件路径
 */

public class DataBaseContext extends ContextWrapper {
    public DataBaseContext(Context base) {
        super(base);
    }

    /**
     * 获得数据库文件路径，如果不存在，就创造
     */
    @Override
    public File getDatabasePath(String name) {
        //判断是否存在sd卡
        String path = null;
        boolean sdExist = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        if (!sdExist) {
            return null;
        } else {
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
            path += "/databases";//数据库所在目录
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            boolean dbExsit = false;
            String paths = path + "/" + name;
            File files = new File(paths);
            if (!files.exists()) {
                try {
                    files.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                dbExsit  = true;
            }
            if (dbExsit){
                return files;
            }else {
                return  super.getDatabasePath(name);
            }

        }
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory);
        return result;
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory);
        return result;
    }
}
