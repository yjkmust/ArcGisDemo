package com.yjkmust.arcgisdemo.Option;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.yjkmust.arcgisdemo.Inter.LocationChangedCallback;
import com.yjkmust.arcgisdemo.MyApp;
import com.yjkmust.arcgisdemo.R;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Shyam on 2016/9/2.
 */
public class GPSOptionClass {
    private static final float GPS_LISTENER_MIN_DISTANCE = 1f;
    private static final int ANIMATION_COUNT = 32;
    private static final float NORTH_START_ROTATION = 90;
    private static final SimpleFillSymbol SYMBOL_FILL_BUFFER = new SimpleFillSymbol(Color.argb(50, 255, 255, 0));

    private LocationChangedCallback locationChangedListener;

    public LocationChangedCallback getLocationChangedListener() {
        return locationChangedListener;
    }

    public void setLocationChangedListener(LocationChangedCallback locationChangedListener) {
        this.locationChangedListener = locationChangedListener;
    }

    private Drawable drawableLocation = MyApp.getInstance().getResources().getDrawable(R.drawable.ic_navigation_black_24dp);

    public Drawable getDrawableLocation() {
        return drawableLocation;
    }

    public void setDrawableLocation(Drawable drawableLocation) {
        this.drawableLocation = drawableLocation;
    }

    private boolean lockedLocation = false;

    public boolean isLockedLocation() {
        return lockedLocation;
    }

    public void setLockedLocation(boolean lockedLocation) {
        this.lockedLocation = lockedLocation;
    }

    private float maxLocationScale = 0;

    public float getMaxLocationScale() {
        return maxLocationScale;
    }

    public void setMaxLocationScale(float minLocationScale) {
        this.maxLocationScale = minLocationScale;
    }

    private boolean starting = false;

    public boolean isStarting() {
        return starting;
    }

    private double offsetX = 0.0;
    private double offsetY = 0.0;

    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }

    public void setOffsetX(double offsetX) {
        this.offsetX = offsetX;
    }

    private MapView mapView;
    private GraphicsLayer markerLayer;
    private GPSProjectType projectType = GPSProjectType.NONE;
    private LocationManager locationManager;//gps定位对象
    private SensorManager sensorManager;//重力感应
    private boolean hasSignal = false;
    private int count = 0;
    private Location lastLocation;
    private Point projectionLocation;
    private int bufferId = -1;
    private int markerId = -1;
    private Lock lock = new ReentrantLock();

    public GPSOptionClass(MapView mapView, GraphicsLayer gpsMarkerLayer, GPSProjectType projectType) {
        SYMBOL_FILL_BUFFER.setOutline(new SimpleLineSymbol(Color.argb(50, 255, 255, 0), 0));

        this.mapView = mapView;
        this.markerLayer = gpsMarkerLayer;
        this.projectType = projectType;

        locationManager = (LocationManager) MyApp.getInstance().getSystemService(Context.LOCATION_SERVICE);
        sensorManager = (SensorManager) MyApp.getInstance().getSystemService(Context.SENSOR_SERVICE);

//        //初始化GPS偏移量
//        ParamService service = new ParamService();
//        offsetX = service.getDoubleValue(ParamService.Param_GPS_X);
//        offsetY = service.getDoubleValue(ParamService.Param_GPS_Y);
    }

    /**
     * GPS信号buffer动画计时器
     */
    private CountDownTimer countDownTimer = new CountDownTimer(2000, 1000 / 16) {

        @Override
        public void onTick(long millisUntilFinished) {
            if (hasSignal) {
                return;
            }
            if (count > ANIMATION_COUNT) {
                count = 0;
            }
            count++;
            Message message = new Message();
            message.what = 3;
            eventHandler.sendMessage(message);
        }

        @Override
        public void onFinish() {
            count = 0;
        }
    };

    /**
     * 事件消息处理
     */
    private Handler eventHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 3:
                    if (lastLocation == null) {
                        MyApp.showToast("正在获取GPS信号");
                        return;
                    }

                    double radius = 1f * mapView.getScale() / 100.0 / (float) ANIMATION_COUNT * (float) count;
                    if (lastLocation != null && lastLocation.getAccuracy() > 0) {
                        radius = lastLocation.getAccuracy() / (float) ANIMATION_COUNT * (float) count;
                    }
                    Polygon polygon = null;
                    try {
                        polygon = GeometryEngine.buffer(projectionLocation,
                                mapView.getSpatialReference(), radius, null);
                    } catch (Exception e) {
                        polygon = null;
                    }
                    if (polygon != null) {
                        if (bufferId > 0) {
                            markerLayer.updateGraphic(bufferId, polygon);
                        }
                    }
                    break;
            }
        }
    };

    /*
	 * 判断GPS状态，是否开启了GPS
	 */
    public boolean isOpened() {
        boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(
                MyApp.getInstance().getContentResolver(), LocationManager.GPS_PROVIDER);
        return gpsStatus;
    }

    /**
     * GPS矫正
     * 如果当前GPS精度大于10米则视为GPS信号不稳定
     * @param destPosition 目标位置
     * @param offset
     * @return 偏移量
     */
    public boolean fixGPS(Point destPosition, double[] offset){
        if (lastLocation == null || lastLocation.getAccuracy() > 1.0f){
            return false;
        }
        Point curPosition = projectionLocation(lastLocation);
        offsetX = destPosition.getX() - curPosition.getX();
        offsetY = destPosition.getY() - curPosition.getY();
        offset[0] = offsetX;
        offset[1] = offsetY;
        return true;
    }

    /**
     * 获取定位点，稳定后才能获取
     * @return
     */
    public Point getProjectionLocation(){
        if (lastLocation == null || lastLocation.getAccuracy() > 10f){
            return null;
        }
        return projectionLocation;
    }

    /**
     * 开启GPS定位
     */
    public void start() {
        if (ActivityCompat.checkSelfPermission(MyApp.getInstance(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MyApp.getInstance(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setSpeedRequired(false);
        String bestProvider = locationManager.getBestProvider(criteria, true);
        //获取最后一次位置显示
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        updateLocation(location);
        locationManager.requestLocationUpdates(bestProvider, 50, GPS_LISTENER_MIN_DISTANCE, locationListener);
        locationManager.addGpsStatusListener(gpsStatusListener);
        // 注册监重力监听事件
        if (sensorManager != null) {
            sensorManager.registerListener(sensorEventListener,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                    SensorManager.SENSOR_DELAY_NORMAL);
            // 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
        }
        starting = true;
    }

    /**
     * 关闭GPS定位
     */
    public void stop() {
        if (ActivityCompat.checkSelfPermission(MyApp.getInstance(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MyApp.getInstance(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(locationListener);
        locationManager.removeGpsStatusListener(gpsStatusListener);
        if (sensorManager != null) {// 取消监听器
            sensorManager.unregisterListener(sensorEventListener);
        }
        countDownTimer.cancel();
        count = 0;
        bufferId = -1;
        markerId = -1;
        hasSignal = false;
        lockedLocation = false;
        if (markerLayer != null){
            markerLayer.removeAll();
        }
        starting = false;
    }

    /**
     * 重力监听对象
     */
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                if (markerId > 0) {
                    // 传感器信息改变时执行该方法
                    float[] values = sensorEvent.values;
                    float x = values[0]; // x轴方向的重力加速度，向右为正
                    PictureMarkerSymbol symbol = (PictureMarkerSymbol) markerLayer.getGraphic(markerId).getSymbol();
                    symbol.setAngle(x + NORTH_START_ROTATION);
                    markerLayer.updateGraphic(markerId, symbol);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    /**
     * GPS状态监听事件
     */
    private GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int statue) {
            if (statue == GpsStatus.GPS_EVENT_STOPPED) {
                return;
            }
            if (!hasSignal) {
                count = 0;
                countDownTimer.cancel();
                countDownTimer.start();
            } else {
                lock.lock();
                hasSignal = false;
                countDownTimer.cancel();
                lock.unlock();
            }
        }
    };

    /**
     * GPS定位监听事件
     */
    private LocationListener locationListener = new LocationListener() {

        public void onLocationChanged(Location location) {
            lock.lock();
            hasSignal = true;
            updateLocation(location);
            lock.unlock();

        }

        public void onProviderDisabled(String provider) {
            MyApp.showToast("GPS服务不可用");
        }

        public void onProviderEnabled(String provider) {

            // Toast.makeText(cxt, "GPS服务正常", Toast.LENGTH_LONG).show();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.OUT_OF_SERVICE:// 信号不在服务区
                    MyApp.showToast("GPS信号不在服务区");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    MyApp.showToast("GPS服务不可用");
                    break;
                case LocationProvider.AVAILABLE:
                    // Toast.makeText(cxt, "GPS服务正常", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }

    };

    /**
     * 更新定位点
     * @param location
     */
    private void updateLocation(final Location location){
        lastLocation = location;
        if (location == null) {
            return;
        }
        projectionLocation = projectionLocation(location);
        if (projectionLocation == null){
            return;
        }
        projectionLocation.setXY(projectionLocation.getX() + offsetX, projectionLocation.getY() + offsetY);
        if (locationChangedListener != null){
            locationChangedListener.onLocationChanged(projectionLocation, location);
        }
        if (markerLayer == null){
            return;
        }
        Geometry buffer = GeometryEngine.buffer(projectionLocation, mapView.getSpatialReference(), lastLocation.getAccuracy(), null);
        //绘制GPS点和误差范围
        if (markerId > 0){
            markerLayer.updateGraphic(bufferId, buffer);
            markerLayer.updateGraphic(markerId, projectionLocation);
        } else{
            bufferId = markerLayer.addGraphic(new Graphic(buffer, SYMBOL_FILL_BUFFER));
            PictureMarkerSymbol symbol = new PictureMarkerSymbol(drawableLocation);
            markerId = markerLayer.addGraphic(new Graphic(projectionLocation, symbol));
        }
        //如果是跟踪定位，则移动定位点到屏幕中央
        if (mapView != null && lockedLocation){
            if (maxLocationScale > 0 && mapView.getScale() > maxLocationScale){
                mapView.zoomToScale(projectionLocation, maxLocationScale);
            } else {
                mapView.centerAt(projectionLocation, true);
            }
        }
    }



    /**
     * 投影转换
     * @param location
     * @return
     */
    private Point projectionLocation(final Location location) {
        if (projectType == GPSProjectType.KM87) {
            return ProjectionClass.projectionLocationForKM87(location);
        } else{
            if (mapView == null){
                return null;
            }
            return ProjectionClass.projectionGPS(location, mapView.getSpatialReference());
        }
    }

    public enum GPSProjectType{
        NONE,
        KM87,
    }
}


