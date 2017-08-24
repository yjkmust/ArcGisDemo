package com.yjkmust.arcgisdemo.Adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yjkmust.arcgisdemo.Bean.PipelineModel;
import com.yjkmust.arcgisdemo.R;

import java.util.List;

/**
 * Created by GEOFLY on 2017/8/24.
 */

public class LayerContentAdapter extends BaseQuickAdapter<PipelineModel, BaseViewHolder> {
    public LayerContentAdapter(List<PipelineModel> data) {
        super(R.layout.item_layer_content, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PipelineModel item) {
//        if (item.getAttributes().get("ID")==null){
//            helper.setText(R.id.tv_titles, "ok");
//        }else {
//            helper.setText(R.id.tv_titles, String.valueOf(item.getAttributes().get("ID")));
//        }
        String msg = "";
        msg = item.getAttributes().get("ID") == null ? (item.getAttributes().get("id") == null ? "值为空" : String.valueOf(item.getAttributes().get("id"))) : String.valueOf(item.getAttributes().get("ID"));
        helper.setText(R.id.tv_titles, msg);
    }
}
