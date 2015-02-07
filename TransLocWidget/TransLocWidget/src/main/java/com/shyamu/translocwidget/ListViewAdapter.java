package com.shyamu.translocwidget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shyamal on 2/6/2015.
 */
public class ListViewAdapter extends BaseAdapter {
    private LayoutInflater mInflater = null;
    private ArrayList<ListViewItem> widgetList = new ArrayList<ListViewItem>();
    private Context mContext;


    private final class ViewHolder {
        TextView tv_agencyName;
        TextView tv_routeName;
        TextView tv_stopName;
    }

    private ViewHolder mHolder = null;

    public ListViewAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return widgetList.size();
    }

    @Override
    public ListViewItem getItem(int position) {
        return widgetList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            mHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_view, null);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder)convertView.getTag();
        }

        mHolder.tv_agencyName = (TextView)convertView.findViewById(R.id.tvAgencyNameItemView);
        mHolder.tv_routeName = (TextView)convertView.findViewById(R.id.tvRouteNameItemView);
        mHolder.tv_stopName = (TextView)convertView.findViewById(R.id.tvStopNameItemView);

        mHolder.tv_agencyName.setText(widgetList.get(position).getAgencyName());
        mHolder.tv_routeName.setText(widgetList.get(position).getRouteName());
        mHolder.tv_stopName.setText(widgetList.get(position).getStopName());

        return convertView;
    }
}
