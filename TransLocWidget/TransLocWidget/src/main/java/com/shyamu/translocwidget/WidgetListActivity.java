package com.shyamu.translocwidget;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shyamu.translocwidget.rest.model.TransLocAgency;
import com.shyamu.translocwidget.TransLocJSON.TransLocRoute;
import com.shyamu.translocwidget.TransLocJSON.TransLocStop;
import com.shyamu.translocwidget.TransLocJSON.TransLocStops;
import com.shyamu.translocwidget.fragments.WidgetListFragment;
import com.shyamu.translocwidget.rest.service.ServiceGenerator;
import com.shyamu.translocwidget.rest.service.TransLocAgenciesClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;


public class WidgetListActivity extends ActionBarActivity implements WidgetListFragment.OnFragmentInteractionListener {
    private static final String FILE_NAME = "WidgetList";
    private static final String TAG = "WidgetListActivity";

    private static ArrivalTimeWidget atw = new ArrivalTimeWidget();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_list);
        Intent intent = getIntent();

        if(intent.hasExtra("starting_fragment")) {
            String startingFragment = intent.getStringExtra("starting_fragment");
            if(startingFragment.equals("AddAgencyFragment")) {
                getFragmentManager().beginTransaction()
                        .add(R.id.container, new AddAgencyFragment())
                        .addToBackStack(null)
                        .commit();
            }
        }
        else if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new WidgetListFragment())
                    .addToBackStack(null)
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
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, addAgencyFragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(String id) {
        Toast.makeText(getApplicationContext(), id, Toast.LENGTH_LONG).show();
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
            //new PopulateAgenciesTask(getActivity(), rootView).execute();
            TransLocAgenciesClient client =  ServiceGenerator.createService(TransLocAgenciesClient.class, Utils.BASE_URL, getString(R.string.mashape_key));
            client.agencies()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            result -> Toast.makeText(getActivity(), Integer.toString(result.size()), Toast.LENGTH_LONG).show(),
                    e -> Log.e(TAG, "ERROR: " + e)
            );
            return rootView;
        }

        class PopulateAgenciesTask extends AsyncTask<Void,Void, List<TransLocAgency>> {

            View mRootView;
            Activity mContext;

            public PopulateAgenciesTask(Activity context, View rootView) {
                mContext = context;
                mRootView = rootView;
            }

            @Override
            protected List<TransLocAgency> doInBackground(Void... params) {
                try {
                    TransLocAgenciesClient client =  ServiceGenerator.createService(TransLocAgenciesClient.class, Utils.BASE_URL, getString(R.string.mashape_key));
                    //List<TransLocAgency> agencies = client.agencies();
                    //Log.v(TAG, agencies.toString());
                    return null;
                } catch (Exception e) {
                    Log.e(TAG, "Error in getting list of agencies from TransLoc");
                    Log.e(TAG, e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<TransLocAgency> translocAgencies) {
                if(translocAgencies != null) {
                    ArrayList<TransLocAgency> agencyList = (ArrayList<TransLocAgency>) translocAgencies;
                    ArrayAdapter<TransLocAgency> agencyArrayAdapter = new ArrayAdapter<TransLocAgency>(mContext, android.R.layout.simple_spinner_dropdown_item, agencyList);
                    agencyListView.setAdapter(agencyArrayAdapter);

                    // Set onclicklistener to open select routes fragment
                    agencyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            TransLocAgency selectedAgency = (TransLocAgency) parent.getItemAtPosition(position);
                            atw.setAgencyID(Integer.toString(selectedAgency.agencyId));
                            atw.setAgencyLongName(selectedAgency.longName);

                            Fragment addRouteFragment = new AddRouteFragment();
                            // Insert the fragment by replacing any existing fragment
                            FragmentManager fragmentManager = mContext.getFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.container, addRouteFragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    });
                } else {
                    Log.e(TAG, "Agencies data was null!");
                }
            }
        }


    }

    public static class AddRouteFragment extends Fragment {

        private final String TAG = this.getTag();
        ListView routeListView;

        public AddRouteFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_add_route, container, false);
            routeListView = (ListView) rootView.findViewById(R.id.lvRouteList);
            new PopulateRoutesTask(getActivity(), rootView).execute();
            return rootView;
        }

        class PopulateRoutesTask extends AsyncTask<Void,Void,ArrayList<TransLocRoute>> {

            View mRootView;
            Activity mContext;

            public PopulateRoutesTask(Activity context, View rootView) {
                mContext = context;
                mRootView = rootView;
            }

            @Override
            protected ArrayList<TransLocRoute> doInBackground(Void... params) {
                String url = Utils.GET_ROUTES_URL + atw.getAgencyID();
                try {
                    //return new ObjectMapper().readValue(Utils.getJsonResponse(url, getString(R.string.mashape_key)), TransLocRoutes.class);
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode rootNode = mapper.readTree(Utils.getJsonResponse(url, getString(R.string.mashape_key)));
                    JsonNode arrayNode = rootNode.get("data").get(atw.getAgencyID());
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    ArrayList<TransLocRoute> array = mapper.readValue(arrayNode.toString(),mapper.getTypeFactory().constructCollectionType(
                            List.class, TransLocRoute.class));
                    return array;
                } catch (Exception e) {
                    Log.e(TAG, "Error in getting list of routes from TransLoc with url: " + url);
                    Log.e(TAG, e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<TransLocRoute> translocRoutesArray) {
                if(translocRoutesArray != null && !translocRoutesArray.isEmpty()) {
                    ArrayAdapter<TransLocRoute> routeArrayAdapter = new ArrayAdapter<TransLocRoute>(mContext, android.R.layout.simple_spinner_dropdown_item, translocRoutesArray);
                    routeListView.setAdapter(routeArrayAdapter);

                    // Set onclicklistener to open select stops fragment
                    routeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            TransLocRoute selectedRoute = (TransLocRoute) parent.getItemAtPosition(position);
                            atw.setRouteID(Integer.toString(selectedRoute.routeID));
                            atw.setRouteName(selectedRoute.toString());

                            Fragment addStopFragment = new AddStopFragment();
                            // Insert the fragment by replacing any existing fragment
                            FragmentManager fragmentManager = mContext.getFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.container, addStopFragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    });
                } else {
                    Log.e(TAG, "Routes data was null or empty!");
                }
            }
        }

    }

    public static class AddStopFragment extends Fragment {

        private final String TAG = this.getTag();
        ListView stopListView;

        public AddStopFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_add_stop, container, false);
            stopListView = (ListView) rootView.findViewById(R.id.lvStopList);
            new PopulateStopsTask(getActivity(), rootView).execute();
            return rootView;
        }

        class PopulateStopsTask extends AsyncTask<Void,Void,TransLocStops> {

            View mRootView;
            Activity mContext;

            public PopulateStopsTask(Activity context, View rootView) {
                mContext = context;
                mRootView = rootView;
            }

            @Override
            protected TransLocStops doInBackground(Void... params) {
                String url = Utils.GET_STOPS_URL + atw.getAgencyID();
                try {
                    return new ObjectMapper().readValue(Utils.getJsonResponse(url, getString(R.string.mashape_key)), TransLocStops.class);
                } catch (Exception e) {
                    Log.e(TAG, "Error in getting list of stops from TransLoc with url: " + url);
                    Log.e(TAG, e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(TransLocStops translocStops) {
                if(translocStops != null ) {
                    ArrayList<TransLocStop> fullStopList = (ArrayList<TransLocStop>) translocStops.data;
                    ArrayList<TransLocStop> stopList = new ArrayList<>();
                    for(TransLocStop stop : fullStopList) {
                        if(stop.routes.contains(Integer.parseInt(atw.getRouteID()))) {
                            stopList.add(stop);
                        }
                    }
                    ArrayAdapter<TransLocStop> stopArrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, stopList);
                    stopListView.setAdapter(stopArrayAdapter);

                    // Set onclicklistener to open select stops fragment
                    stopListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ObjectMapper mapper = new ObjectMapper();
                            ArrayList<ArrivalTimeWidget> listViewArray = null;
                            try {
                                listViewArray = Utils.getArrivalTimeWidgetsFromStorage(mContext);
                            } catch (IOException e) {
                                Log.e(TAG, "Error in getting previous widget list", e);
                                listViewArray = new ArrayList<>();
                            }
                            TransLocStop selectedStop = (TransLocStop) parent.getItemAtPosition(position);
                            atw.setStopID((Integer.toString(selectedStop.stopId)));
                            atw.setStopName(selectedStop.toString());
                            listViewArray.add(atw);
                            try {
                                String value = mapper.writeValueAsString(listViewArray);
                                Log.v(TAG, value);
                                Utils.writeData(mContext, value);
                            } catch (Exception e) {
                                Log.e(TAG, "Error in writing widget list to storage");
                                Log.e(TAG, e.getMessage());
                            }

                            // Insert the fragment by replacing any existing fragment
                            FragmentManager fragmentManager = mContext.getFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.container, new WidgetListFragment())
                                    .commit();
                        }
                    });
                } else {
                    Log.e(TAG, "Stops data was null or empty!");
                }
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
