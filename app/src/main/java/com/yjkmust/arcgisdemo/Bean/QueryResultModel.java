package com.yjkmust.arcgisdemo.Bean;

import com.esri.core.geometry.Geometry;

import java.util.Map;

/**
 * Created by Shyam on 2016/12/26.
 */

public class QueryResultModel {
    private String layerName;
    private long featureId;
    private Geometry geometry;
    private Map<String, Object> attributes;

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    public long getFeatureId() {
        return featureId;
    }

    public void setFeatureId(long featureId) {
        this.featureId = featureId;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
