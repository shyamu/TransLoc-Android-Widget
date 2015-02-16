package com.shyamu.translocwidget;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.shyamu.translocwidget.fragments.WidgetListFragment;

public class WidgetConfigurationPopupActivity extends Activity {

    ListView lv_widgetList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_list);
        WidgetListFragment fragment;
        if (savedInstanceState != null) {
            fragment = (WidgetListFragment) getFragmentManager().findFragmentByTag("WidgetListFragment");
        } else {
            fragment = new WidgetListFragment();

        }
        getFragmentManager().beginTransaction()
                .add(R.id.container, fragment, "WidgetListFragment")
                .addToBackStack(null)
                .commit();

        lv_widgetList = fragment.getListView();
        lv_widgetList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrivalTimeWidget selectedWidget = (ArrivalTimeWidget) parent.getSelectedItem();
                Toast.makeText(getApplicationContext(), selectedWidget.getArrivalTimesUrl(), Toast.LENGTH_LONG).show();
            }
        });

    }

}
