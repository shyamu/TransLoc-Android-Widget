package com.shyamu.translocwidget;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shyamu.translocwidget.TransLocJSON.TransLocAgencies;
import com.shyamu.translocwidget.TransLocJSON.TransLocAgency;

import java.util.ArrayList;
import java.util.Collections;


public class WidgetListActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_list);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_widget_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_addWidget) {
            Fragment addAgencyFragment = new AddAgencyFragment();
            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, addAgencyFragment)
                    .commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_widget_list, container, false);
            ListView widgetListView = (ListView) rootView.findViewById(R.id.lvWidgetList);
            ListViewAdapter widgetListViewAdapter = new ListViewAdapter(getActivity());
            widgetListView.setAdapter(widgetListViewAdapter);
            return rootView;
        }
    }

    public static class AddAgencyFragment extends Fragment {

        private final String TAG = this.getTag();
        ListView agencyListView;

        public AddAgencyFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_add_agency, container, false);
            agencyListView = (ListView) rootView.findViewById(R.id.lvAgencyList);
            new PopulateAgenciesTask(getActivity(), rootView).execute();
            return rootView;
        }

        class PopulateAgenciesTask extends AsyncTask<Void,Void,TransLocAgencies> {

            View mRootView;
            Activity mContext;

            public PopulateAgenciesTask(Activity context, View rootView) {
                mContext = context;
                mRootView = rootView;
            }

            @Override
            protected TransLocAgencies doInBackground(Void... params) {
                String url = Utils.GET_AGENCIES_URL;
                try {
                    return new ObjectMapper().readValue(Utils.getJsonResponse(url, getString(R.string.mashape_key)), TransLocAgencies.class);
                } catch (Exception e) {
                    Log.e(TAG, "Error in getting list of agencies from TransLoc with url: " + url);
                    Log.e(TAG, e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(TransLocAgencies translocAgencies) {
                if(translocAgencies != null) {
                    ArrayList<TransLocAgency> agencyList = (ArrayList<TransLocAgency>) translocAgencies.getData();
                    ArrayAdapter<TransLocAgency> agencyArrayAdapter = new ArrayAdapter<TransLocAgency>(mContext, android.R.layout.simple_spinner_dropdown_item, agencyList);
                    agencyListView.setAdapter(agencyArrayAdapter);
                } else {
                    //TODO error!
                }
            }
        }
    }
}
