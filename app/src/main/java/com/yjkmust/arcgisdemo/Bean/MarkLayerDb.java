package com.yjkmust.arcgisdemo.Bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by GEOFLY on 2017/8/4.
 */
@Entity
public class MarkLayerDb {
    @Id(autoincrement = true)
    private Long  ID;
    private String LabelText;
    private String ShapeJson;
    private String Data1;
    private String Data2;
    private int Data3;

    @Generated(hash = 947690969)
    public MarkLayerDb(Long ID, String LabelText, String ShapeJson, String Data1,
            String Data2, int Data3) {
        this.ID = ID;
        this.LabelText = LabelText;
        this.ShapeJson = ShapeJson;
        this.Data1 = Data1;
        this.Data2 = Data2;
        this.Data3 = Data3;
    }

    @Generated(hash = 546399166)
    public MarkLayerDb() {
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long  ID) {
        this.ID = ID;
    }

    public String getLabelText() {
        return LabelText;
    }

    public void setLabelText(String labelText) {
        LabelText = labelText;
    }

    public String getShapeJson() {
        return ShapeJson;
    }

    public void setShapeJson(String shapeJson) {
        ShapeJson = shapeJson;
    }

    public String getData1() {
        return Data1;
    }

    public void setData1(String data1) {
        Data1 = data1;
    }

    public String getData2() {
        return Data2;
    }

    public void setData2(String data2) {
        Data2 = data2;
    }

    public int getData3() {
        return Data3;
    }

    public void setData3(int data3) {
        Data3 = data3;
    }

}
