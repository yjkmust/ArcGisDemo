package com.yjkmust.arcgisdemo.Bean;

import com.esri.core.geometry.Geometry;

import java.util.Map;

public class PipelineModel {
    private long id;
    private int type;
    private Geometry geometry;
    private Map<String, Object> attributes;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

	@Override
	public String toString() {
		return "PipelineModel [id=" + id + ", type=" + type + ", geometry=" + geometry + ", attributes=" + attributes
				+ "]";
	}
    
}
