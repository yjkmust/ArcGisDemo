package com.yjkmust.arcgisdemo.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.esri.android.map.Layer;
import com.esri.android.map.MapView;
import com.yjkmust.arcgisdemo.MyApp;
import com.yjkmust.arcgisdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shyam on 2016/8/26.
 */
public class LayerVisibilityAdapter extends BaseAdapter {

    private MapView mapView;
    private List<Layer> allLayers;
    private LayoutInflater inflater;

    public LayerVisibilityAdapter(MapView mapView){
        inflater = LayoutInflater.from(MyApp.getInstance());
        this.mapView = mapView;
        allLayers = new ArrayList<Layer>();
        if (mapView != null){
            for (Layer layer : mapView.getLayers()) {
                if (layer.getName() == null || layer.getName().length() <= 0){
                    continue;
                }
                allLayers.add(0, layer);
            }
        }
    }

    @Override
    public int getCount() {
        return allLayers.size();
    }

    @Override
    public Object getItem(int i) {
        return allLayers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return ((Layer)getItem(i)).getID();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Layer layer = (Layer)getItem(i);
        View v = inflater.inflate(R.layout.list_item_check, null);
        TextView text = (TextView)v.findViewById(R.id.text);
        Switch op = (Switch) v.findViewById(R.id.opChecked);
        op.setTag(layer);
        op.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Layer layer = (Layer)compoundButton.getTag();
                if (layer != null){
                    layer.setVisible(b);
                }
            }
        });
        text.setText(layer.getName());
        op.setChecked(layer.isVisible());

        return v;
    }
}
