package com.yjkmust.arcgisdemo.Utils;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by GEOFLY on 2017/8/8.
 */

 public class  LayerUtils {
    private String TAG = "yaojie";
    public List<String> getPath(){
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LayerShape";
//        String filePaths = Environment.getExternalStorageDirectory().getAbsolutePath() + "/YNMRP/汇总数据";
//        String filePaths = "file:///storage/sdcard0/LayerShape";
        List list = new ArrayList();
        File file = new File(filePath);
        if (!file.exists()){
            file.mkdirs();
        }
        File[] files = file.listFiles();
        if (files!=null){
            for (File fi :files) {
                if (fi.getAbsolutePath().endsWith(".shp")){
                    list.add(fi.getAbsolutePath());
                }
            }
        }
        return list;
    }
    public String getXZQPath(){
        String XZQPath= Environment.getExternalStorageDirectory().getAbsolutePath() + "/LayerShape/行政区.shp";
        return XZQPath;
    }

}
