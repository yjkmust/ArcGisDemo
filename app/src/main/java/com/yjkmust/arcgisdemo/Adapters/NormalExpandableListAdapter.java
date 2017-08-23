package com.yjkmust.arcgisdemo.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.yjkmust.arcgisdemo.Bean.CityBean;
import com.yjkmust.arcgisdemo.Bean.CityJsonBean;
import com.yjkmust.arcgisdemo.Inter.onGroupExpandedListener;
import com.yjkmust.arcgisdemo.MyApp;
import com.yjkmust.arcgisdemo.R;

import java.util.List;

/**
 * @author Richie on 2017.07.31
 *         普通的 ExpandableListView 的适配器
 */
public class NormalExpandableListAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "NormalExpandableListAda";
    private List<CityBean> groupData;
    private List<List<CityJsonBean.StateBean.CityBean>> childData;
    private onGroupExpandedListener mOnGroupExpandedListener;
    private Listener listener;

    public NormalExpandableListAdapter(List<CityBean> groupData, List<List<CityJsonBean.StateBean.CityBean>> childData) {
        this.groupData = groupData;
        this.childData = childData;
    }

    public void setOnGroupExpandedListener(onGroupExpandedListener onGroupExpandedListener) {
        mOnGroupExpandedListener = onGroupExpandedListener;
    }

    @Override
    public int getGroupCount() {
        return groupData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childData.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupData.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childData.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View
            convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expand_group_normal, parent, false);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.tvTitle = (TextView) convertView.findViewById(R.id.label_group_normal);
            groupViewHolder.tvNull = (TextView) convertView.findViewById(R.id.tv_null);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        groupViewHolder.tvTitle.setText(groupData.get(groupPosition).getCity());
        groupViewHolder.tvNull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.Position(groupPosition);
                MyApp.showToast("点击的是"+groupPosition);
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View
            convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expand_child, parent, false);
            childViewHolder = new ChildViewHolder();
            childViewHolder.tvTitle = (TextView) convertView.findViewById(R.id.label_expand_child);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        childViewHolder.tvTitle.setText(childData.get(groupPosition).get(childPosition).getName());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        Log.d(TAG, "onGroupExpanded() called with: groupPosition = [" + groupPosition + "]");
        if (mOnGroupExpandedListener != null) {
            mOnGroupExpandedListener.onGroupExpanded(groupPosition);
        }
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        Log.d(TAG, "onGroupCollapsed() called with: groupPosition = [" + groupPosition + "]");
    }

    private static class GroupViewHolder {
        TextView tvTitle;
        TextView tvNull;
    }

    private static class ChildViewHolder {
        TextView tvTitle;
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }
    public interface Listener{
        void Position(int position);
    }
}
