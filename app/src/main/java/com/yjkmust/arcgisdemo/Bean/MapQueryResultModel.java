package com.yjkmust.arcgisdemo.Bean;

/**
 * Created by GEOFLY on 2017/8/4.
 */

public class MapQueryResultModel {
    private int index;
    private String text;
    private Object value;
    private Object value1;
    private int type;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue1() {
        return value1;
    }

    public void setValue1(Object value1) {
        this.value1 = value1;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
