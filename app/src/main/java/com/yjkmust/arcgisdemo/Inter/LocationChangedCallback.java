package com.yjkmust.arcgisdemo.Inter;

import android.location.Location;

import com.esri.core.geometry.Point;

/**
 * Created by Shyam on 2016/9/2.
 */
public interface LocationChangedCallback {

    public void onLocationChanged(final Point projectionPosition, final Location gpsPosition);
}
