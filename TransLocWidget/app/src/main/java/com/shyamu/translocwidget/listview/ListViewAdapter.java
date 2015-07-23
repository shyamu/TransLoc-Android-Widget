package com.shyamu.translocwidget.listview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shyamu.translocwidget.R;
import com.shyamu.translocwidget.bl.ArrivalTimeWidget;
import com.shyamu.translocwidget.bl.Utils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Shyamal on 2/6/2015.
 */
public class ListViewAdapter extends BaseAdapter {
    private static final String TAG = "ListViewAdapter";
    private LayoutInflater mInflater = null;
    private ArrayList<ArrivalTimeWidget> widgetList = new ArrayList<>();
    private Context mContext;

    private final class ViewHolder {
        TextView tv_agencyName;
        TextView tv_routeName;
        TextView tv_stopName;
        RelativeLayout rl_backgroundColor;
        RelativeLayout rl_TextColor;
    }

    private ViewHolder mHolder = null;

    public ListViewAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        try {
            widgetList = Utils.getArrivalTimeWidgetsFromStorage(context);
        } catch (IOException e) {
            Log.e(TAG, "error in reading widget list from storage", e);
        }
    }

    @Override
    public int getCount() {
        return widgetList.size();
    }

    @Override
    public ArrivalTimeWidget getItem(int position) {
        return  widgetList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return -1;
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

        mHolder.rl_backgroundColor = (RelativeLayout) convertView.findViewById(R.id.rlBackgroundColor);
        mHolder.rl_TextColor = (RelativeLayout) convertView.findViewById(R.id.rlTextColor);
        mHolder.tv_agencyName = (TextView)convertView.findViewById(R.id.tvAgencyNameItemView);
        mHolder.tv_routeName = (TextView)convertView.findViewById(R.id.tvRouteNameItemView);
        mHolder.tv_stopName = (TextView)convertView.findViewById(R.id.tvStopNameItemView);

        ArrivalTimeWidget arrivalTimeWidget = widgetList.get(position);

        // Set colors
        mHolder.rl_backgroundColor.setBackgroundColor(arrivalTimeWidget.getBackgroundColor());
        mHolder.rl_TextColor.setBackgroundColor(arrivalTimeWidget.getTextColor());

        // Set text content
        mHolder.tv_agencyName.setText(arrivalTimeWidget.getAgencyLongName());
        mHolder.tv_routeName.setText(arrivalTimeWidget.getRouteName());
        mHolder.tv_stopName.setText(arrivalTimeWidget.getStopName());

        return convertView;
    }

    public int addToWidgetList(ArrivalTimeWidget widget) {
        widgetList.add(widget);
        return widgetList.size();
    }

    public void setWidgetList(ArrayList<ArrivalTimeWidget> newWidgetList) {
        this.widgetList = newWidgetList;
    }
}
