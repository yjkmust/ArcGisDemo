package com.yjkmust.arcgisdemo.Utils;

import android.content.Context;

import com.yjkmust.arcgisdemo.Bean.MarkLayerDb;
import com.yjkmust.arcgisdemo.gen.DaoSession;
import com.yjkmust.arcgisdemo.gen.MarkLayerDbDao;

import java.util.List;

/**
 * Created by GEOFLY on 2017/8/4.
 */

public class DbUtils {
    public static DbUtils dbUtils;
    public static DaoSession daoSession;
    public static MarkLayerDbDao dbDao;
    public static Context context;
    public static DbUtils getDbUtils(Context context){
        if (dbUtils==null){
            dbUtils=new DbUtils();
            if (context==null){
                context=context.getApplicationContext();
            }
            daoSession = DaoManger.getDaoSession(context);
            dbDao = daoSession.getMarkLayerDbDao();
        }
        return dbUtils;
    }
    public void insertMarkLayer(MarkLayerDb db){
        dbDao.insert(db);
    }
    private void insertListMarkLayer(List<MarkLayerDb> users){
        dbDao.insertInTx(users);
    }
    public List<MarkLayerDb> loadAllMarkLayer(){
        return dbDao.loadAll();
    }
    public void DelAllMarkLayer(){
        dbDao.deleteAll();
    }
    /**
     * 根据ID删除数据
     */
    public void deleteOne(Long id){
        dbDao.deleteByKey(id);

    }
    /**
     * g根据ID批量删除数据
     */
    public void deleteOnes(List<Long> lists){
        for (Long list : lists){
            dbDao.deleteByKey(list);
        }
    }

    /**
     * 根据用户类删除信息
     */
    public void deleteMarkLayer(String labeltext){
        List<MarkLayerDb> list = dbDao.queryBuilder().where(MarkLayerDbDao.Properties.LabelText.eq(labeltext)).build().list();
        dbDao.deleteInTx(list);
//        userDao.delete(user);
    }
    public void deleteMap(String mapview){
        List<MarkLayerDb> list = dbDao.queryBuilder().where(MarkLayerDbDao.Properties.Data2.eq(mapview)).build().list();
        dbDao.deleteInTx(list);
//        userDao.delete(user);
    }
    public void deleteMarkLayer(MarkLayerDb user){
        dbDao.delete(user);
    }

    /**
     * 根据用户ID，取出用户信息
     */
    public MarkLayerDb loadMarkLayer(long id){
        return dbDao.load(id);
    }
    /**
     * 根据用户信息，修改信息
     */
    public long ChangeUser(MarkLayerDb user){
        return dbDao.insertOrReplace(user);
//        return userDao.update(user);
    }
    public void ChangePeoples(final List<MarkLayerDb> list){
        if (list==null||list.isEmpty()){
            return;
        }
        dbDao.getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                for (int i=0;i<list.size();i++){
                    MarkLayerDb user = list.get(i);
                    dbDao.insertOrReplace(user);
                }
            }
        });
    }
    /**
     * queryBuilder查询数据
     */
    public List<MarkLayerDb> QureyBuilder(MarkLayerDb user){
        List<MarkLayerDb> list = dbDao.queryBuilder().where(MarkLayerDbDao.Properties.LabelText.eq(user.getLabelText()), MarkLayerDbDao.Properties.ShapeJson.eq(user.getShapeJson())).orderAsc(MarkLayerDbDao.Properties.ID)
                .build().list();
        return list;
    }
    public List<MarkLayerDb> QureyBuilderByMap(String user){
        List<MarkLayerDb> list = dbDao.queryBuilder().where(MarkLayerDbDao.Properties.Data2.eq(user))
                .build().list();
        return list;
    }
    /**
     * 批量删除数据
     */

}
