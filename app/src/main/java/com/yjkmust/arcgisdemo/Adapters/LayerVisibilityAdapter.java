package com.yjkmust.arcgisdemo.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.yjkmust.arcgisdemo.MyApp;
import com.yjkmust.arcgisdemo.R;

import java.util.List;


/**
 * Created by Shyam on 2016/8/26.
 */
public class LayerVisibilityAdapter extends BaseAdapter {
    private List<Layer> allLayers;
    private LayoutInflater inflater;
    public LayerVisibilityAdapter(List<Layer> allLayers){
        inflater = LayoutInflater.from(MyApp.getInstance());
        this.allLayers = allLayers;
        }
    @Override
    public int getCount() {
        return allLayers.size();
    }

    @Override
    public Layer getItem(int i) {
        return allLayers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return ((Layer)getItem(i)).getID();
    }

    @Override
    public View getView(final int position, View convertview, ViewGroup viewGroup) {
//        View view;
        ViewHolder viewHolder;
        if(convertview==null){
            convertview = inflater.inflate(R.layout.list_item_check, null);
            viewHolder = new ViewHolder();
            viewHolder.textView =(TextView) convertview.findViewById(R.id.tv_text);
            viewHolder.imageView = convertview.findViewById(R.id.iv_content);
            viewHolder.mSwitch = (Switch) convertview.findViewById(R.id.opChecked);
            convertview.setTag(viewHolder);
        }else {
//            view = convertview;
            viewHolder = (ViewHolder) convertview.getTag();
        }
        final Layer layer = getItem(position);
        if(layer instanceof GraphicsLayer || layer instanceof ArcGISLocalTiledLayer){
            viewHolder.imageView.setVisibility(View.GONE);
        } else {
            if(layer.getName().equals("行政区")){
                viewHolder.imageView.setVisibility(View.GONE);
            } else {
                viewHolder.imageView.setVisibility(View.VISIBLE);
            }
        }
        viewHolder.mSwitch.setTag(allLayers.get(position));
        viewHolder.mSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Layer layer = (Layer)compoundButton.getTag();
                if (layer != null){
                    layer.setVisible(b);
                }
            }
        });
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Layer layer = getItem(position);
                listener.content(layer,position,layer.getName());
            }
        });
        viewHolder.textView.setText(allLayers.get(position).getName());
        viewHolder.mSwitch.setChecked(allLayers.get(position).isVisible());
        return convertview;
    }
    private ContentListener listener;
    public void setContentListener(ContentListener listener){
        this.listener = listener;
    }
    public interface ContentListener{
        void content(Layer layer, int position,String name);
    }
    class ViewHolder{
        TextView textView;
        ImageView imageView;
        Switch mSwitch;
    }
}
