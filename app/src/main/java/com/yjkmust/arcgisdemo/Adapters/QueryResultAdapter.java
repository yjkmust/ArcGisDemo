package com.yjkmust.arcgisdemo.Adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yjkmust.arcgisdemo.Bean.MapQueryResultModel;
import com.yjkmust.arcgisdemo.MyApp;
import com.yjkmust.arcgisdemo.R;

import java.util.List;


/**
 * Created by Shyam on 2016/9/18.
 */
public class QueryResultAdapter extends BaseAdapter {

    LayoutInflater inflater;
    List<MapQueryResultModel> items = null;
    boolean canDelete;

    public boolean isCanDelete() {
        return canDelete;
    }

    public QueryResultAdapter(List<MapQueryResultModel> source, boolean canDelete){
        this.canDelete = canDelete;
        this.items = source;
        inflater = LayoutInflater.from(MyApp.getInstance());
    }

    @Override
    public int getCount() {
        if (items != null){
            return items.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        if (items != null){
            return items.get(i);
        }
        return null;
    }

    public Object getItemByValue(int value){
        if (items != null){
            for (MapQueryResultModel item : items){
                if (item.getValue().equals(value)){
                    return item;
                }
            }
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = null;
        if (view != null){
            v = view;
        } else{
            v = inflater.inflate(R.layout.list_item_head_image, null);
        }
        MapQueryResultModel model = (MapQueryResultModel)getItem(i);
        ImageView imageView = (ImageView) v.findViewById(R.id.image);
        TextView textView = (TextView) v.findViewById(R.id.text);
        if (model.getValue1() != null && model.getValue1() instanceof Drawable){
            imageView.setImageDrawable((Drawable) model.getValue1());
        } else {
            imageView.setImageDrawable(MyApp.getInstance().getResources().getDrawable(R.drawable.ic_place_black_24dp));
        }
        textView.setText(model.getText());
        return v;
    }

    @Override
    public int getItemViewType(int position) {
        if (canDelete){
            return 1;
        } else{
            return 0;
        }
//        return super.getItemViewType(position);
    }

    public void deleteItem(int position){
        items.remove(position);
        notifyDataSetChanged();
    }

    public void deleteItem2(Object item){
        items.remove(item);
        notifyDataSetChanged();
    }
}
