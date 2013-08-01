package com.shyamu.translocwidget;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shyamu.translocwidget.TransLocJSON.TransLocAgencies;
import com.shyamu.translocwidget.TransLocJSON.TransLocAgency;
import com.shyamu.translocwidget.TransLocJSON.TransLocArrival;
import com.shyamu.translocwidget.TransLocJSON.TransLocArrivalEstimate;
import com.shyamu.translocwidget.TransLocJSON.TransLocArrivalEstimates;
import com.shyamu.translocwidget.TransLocJSON.TransLocRoute;
import com.shyamu.translocwidget.TransLocJSON.TransLocStop;
import com.shyamu.translocwidget.TransLocJSON.TransLocStops;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.DateTime;
import org.joda.time.Minutes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class WidgetConfigurationActivity extends Activity {

    private static final String AGENCIES_URL = "http://api.transloc.com/1.1/agencies.json";
    private static final String ROUTES_URL = "http://api.transloc.com/1.1/routes.json?agencies=";
    private static final String STOPS_URL = "http://api.transloc.com/1.1/stops.json?agencies=";

    static SharedPreferences settings;
    static SharedPreferences.Editor editor;

    private ArrayList<TransLocStop> fullStopList = new ArrayList<TransLocStop>();

    private int currentAgencyId;
    private int currentRouteId;
    private int currentStopId;

    private String agencyLongName;
    private String agencyShortName;
    private String routeShortName;
    private String routeLongName;
    private String stopName;

    private int mAppWidgetId = 0;



    Spinner sSelectAgency, sSelectRoute, sSelectStop;
    Button bReset, bMakeWidget;
    TextView tvHelpMessage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration_activity);

        // Getting references to Spinners and Buttons
        sSelectAgency = (Spinner) findViewById(R.id.sSelectAgency);
        sSelectRoute = (Spinner) findViewById(R.id.sSelectRoute);
        sSelectStop = (Spinner) findViewById(R.id.sSelectStop);
        bReset = (Button) findViewById(R.id.bReset);
        bMakeWidget = (Button) findViewById(R.id.bMakeWidget);
        tvHelpMessage = (TextView) findViewById(R.id.tvHelp);

        Log.v("DEBUG","in onCreate");
        settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        editor = settings.edit();


        // Populate agency spinner
        PopulateAgenciesTask task = new PopulateAgenciesTask();
        task.execute();

        // Defining a click event listener for the button "Reset"
        OnClickListener setResetClickedListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                doReset();
            }
        };

        // Defining a click event listener for the button "Make Widget"
        OnClickListener setMakeWidgetClickedListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                doMakeWidget();
            }
        };

        // On click listener for help text
        OnClickListener setHelpClickedListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.showAlertDialog(WidgetConfigurationActivity.this, "Why can't I find my agency?", getString(R.string.help_dialog));
            }
        };


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {

            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // Setting the click listener on the buttons
        bReset.setOnClickListener(setResetClickedListener);
        bMakeWidget.setOnClickListener(setMakeWidgetClickedListener);
        tvHelpMessage.setOnClickListener(setHelpClickedListener);
    }

    private void doReset() {
        PopulateAgenciesTask task = new PopulateAgenciesTask();
        task.execute();

      //  agencyArrayAdapter.notifyDataSetChanged();
      //  routeArrayAdapter.notifyDataSetChanged();
      //  stopArrayAdapter.notifyDataSetChanged();


    }

    private void doMakeWidget() {
        Log.v("TESTING","Now testing variables");


        // Get arrival time
        PopulateArrivalTask task = new PopulateArrivalTask();
        task.execute();




    }

    private class PopulateAgenciesTask extends AsyncTask<Void, Void, TransLocAgencies> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(WidgetConfigurationActivity.this,"Loading","Please Wait...");
            Log.v("DEBUG","populating agencies");

        }

        protected TransLocAgencies doInBackground(Void... voids) {


            try {
                return new ObjectMapper().readValue(Utils.getJsonResponse(AGENCIES_URL),TransLocAgencies.class);
            } catch (IOException e) {
                Log.e("JSON", "ERROR in getting JSON data");
                e.printStackTrace();
                return null;
            }




        }

        @Override
        protected void onPostExecute(final TransLocAgencies agencyList) {
            if(agencyList == null) Log.e("JSON", "error in getting list of agencies");
            else {
                sSelectAgency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                        currentAgencyId = agencyList.data.get(pos).agencyId;
                        agencyLongName = agencyList.data.get(pos).longName;
                        agencyShortName = agencyList.data.get(pos).shortName;
                        Log.v("HELLO", "current id " + currentAgencyId);
                        editor.putString("AgencyLongName",agencyLongName);
                        editor.putString("AgencyShortName",agencyShortName);
                        editor.putInt("AgencyID",currentAgencyId);
                        new PopulateRoutesTask().execute();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        // do nothing
                    }
                });
                ArrayAdapter<TransLocAgency> agencyArrayAdapter = new ArrayAdapter<TransLocAgency>(getBaseContext(),android.R.layout.simple_list_item_1,agencyList.data);
                sSelectAgency.setAdapter(agencyArrayAdapter);
            }

            dialog.dismiss();

        }

    }

    private class PopulateRoutesTask extends AsyncTask<Void, Void, ArrayList<TransLocRoute>> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {

            dialog = ProgressDialog.show(WidgetConfigurationActivity.this,"Loading","Please Wait...");


        }

        protected ArrayList<TransLocRoute> doInBackground(Void... voids) {
            try {
                Map<String,Object> routeMap = new ObjectMapper().readValue(Utils.getJsonResponse(ROUTES_URL+currentAgencyId),Map.class);
                Map<String,Object> agencyMap = (Map) routeMap.get("data");
                List<Map<String,Object>> routeList=(List)agencyMap.get(Integer.toString(currentAgencyId));
                final ArrayList<TransLocRoute> routesArrayList=new ArrayList<TransLocRoute>();

                if(routeList == null)
                {
                    Log.v("DEBUG", "routelist null");
                    return null;
                } else {
                    Log.v("DEBUG", "routelist not null");

                    for(Map<String,Object> route:routeList){
                        routesArrayList.add(new TransLocRoute(Integer.parseInt((String)route.get("route_id")),(String)route.get("short_name"),(String)route.get("long_name")));
                    }
                    return routesArrayList;
                }


            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPostExecute(final ArrayList<TransLocRoute> routesArrayList) {
            new PopulateStopsTask().execute();
            if(routesArrayList == null) {
                Log.e("JSON", "error in getting list of routes");
                dialog.dismiss();
                Utils.showAlertDialog(WidgetConfigurationActivity.this, "Error - No Routes Available", "No routes are currently available for the agency you have selected. Please try again later when buses are running.");
                // empty routes spinner
                ArrayList<String> arr = new ArrayList<String >();
                sSelectRoute.setAdapter(new ArrayAdapter<String>(WidgetConfigurationActivity.this,android.R.layout.simple_dropdown_item_1line,arr ));

            }
            else {
                sSelectRoute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                        currentRouteId = routesArrayList.get(pos).id;
                        routeLongName = routesArrayList.get(pos).longName;
                        routeShortName = routesArrayList.get(pos).shortName;
                        editor.putString("RouteLongName", routeLongName);
                        editor.putString("RouteShortName", routeShortName);
                        editor.putInt("RouteID", currentRouteId);
                        new FilterStopListTask().execute(fullStopList);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                ArrayAdapter<TransLocRoute> routeArrayAdapter = new ArrayAdapter<TransLocRoute>(getBaseContext(), android.R.layout.simple_list_item_1, routesArrayList);
                sSelectRoute.setAdapter(routeArrayAdapter);

                dialog.dismiss();


            }
        }

    }

    private class PopulateStopsTask extends AsyncTask<Void, Void, TransLocStops> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {

            dialog = ProgressDialog.show(WidgetConfigurationActivity.this,"Loading","Please Wait...");

        }

        protected TransLocStops doInBackground(Void... voids) {

            try {
                return new ObjectMapper().readValue(Utils.getJsonResponse(STOPS_URL+currentAgencyId),TransLocStops.class);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(TransLocStops stopList) {

            fullStopList.clear();
            if(stopList != null) {
                fullStopList.addAll(stopList.data);
            } else {
                Log.e("JSON", "error in getting stops list");
            }
            new FilterStopListTask().execute(fullStopList);

            dialog.dismiss();


        }



    }

    private class FilterStopListTask extends AsyncTask<ArrayList<TransLocStop>,String,ArrayList<TransLocStop>>{
        @Override
        protected ArrayList<TransLocStop> doInBackground(ArrayList<TransLocStop>... fullStopList){
            if(fullStopList.length==1){
                if(fullStopList[0]==null){
                    System.out.println("Null!");
                }
                ArrayList<TransLocStop> currentRouteStopList=new ArrayList<TransLocStop>();
                for(int i=fullStopList[0].size()-1;i>=0;i--){
                    if(fullStopList[0].get(i).routes.contains(currentRouteId)){
                        currentRouteStopList.add(fullStopList[0].get(i));
                    }
                }
                return currentRouteStopList;
            }else{
                throw new IllegalArgumentException("Sorry, one arg only.");
            }
        }

        @Override
        protected void onPostExecute(final ArrayList<TransLocStop> currentRouteStopList){
            ArrayAdapter<TransLocStop> stopArrayAdapter = new ArrayAdapter<TransLocStop>(getBaseContext(), android.R.layout.simple_list_item_1, currentRouteStopList);
            sSelectStop.setAdapter(stopArrayAdapter);

            sSelectStop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                    currentStopId = currentRouteStopList.get(pos).stopId;
                    stopName = currentRouteStopList.get(pos).name;
                    editor.putInt("StopID", currentStopId);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            //if(stopArrayAdapter.isEmpty()) {
            //    Utils.showAlertDialog(WidgetConfigurationActivity.this, "Error - No Stops Available", "No stops are currently available for the route you have selected. Please try again later when buses are running.");
           // }

        }
    }


    private class PopulateArrivalTask extends AsyncTask<Void, Void, TransLocArrivalEstimates> {

        ProgressDialog dialog;

        private int minutes = -1;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(WidgetConfigurationActivity.this,"Making Widget","Please Wait...");

        }

        protected TransLocArrivalEstimates doInBackground(Void... voids) {

            String url = "http://api.transloc.com/1.1/arrival-estimates.json?agencies=" + currentAgencyId + "&routes=" + currentRouteId + "&stops=" + currentStopId;
            // URL is stored as urlXX with XX being the appwidget ID
            editor.putString("url" + mAppWidgetId, url).commit();

            Log.v("ConfigActivity", "URL: " + url);

            //String response = getJsonResponse(url);


            try {
                return new ObjectMapper().readValue(Utils.getJsonResponse(url),TransLocArrivalEstimates.class);

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }



        }

        @Override
        protected void onPostExecute(TransLocArrivalEstimates arrivalEstimatesList) {
            Date currentTimeUTC;
            Date arrivalTimeUTC;

            Log.v("DEBUG", "size = " + arrivalEstimatesList.data.size());

            if(arrivalEstimatesList.data.isEmpty() || arrivalEstimatesList == null)
            {
                Utils.showAlertDialog(WidgetConfigurationActivity.this, "Error - No Arrival Times","No arrival times are currently available for the route and stop you have selected. Please try again later when buses are running.");
                dialog.dismiss();
            } else {

                TransLocArrivalEstimate arrivalEstimate = arrivalEstimatesList.data.get(0);
                TransLocArrival arrival = arrivalEstimate.arrivals.get(0);
                currentTimeUTC = arrivalEstimatesList.generatedOn;
                arrivalTimeUTC = arrival.arrivalAt;
                Log.v("DEBUG","current time: " + currentTimeUTC + " ... " + "arrival time: " + arrivalTimeUTC);
                minutes = Utils.getMinutesBetweenTimes(currentTimeUTC,arrivalTimeUTC);


                // Getting an instance of WidgetManager
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getBaseContext());

                // Instantiating the class RemoteViews with widget_layout
                RemoteViews views = new RemoteViews(getBaseContext().getPackageName(), R.layout.widget_layout);

                //Set the time remaining of the widget
                views.setTextViewText(R.id.tvRemainingTime, Integer.toString(minutes));
                if(minutes < 1) views.setTextViewText(R.id.tvRemainingTime, "<1");
                if(minutes < 2) views.setTextViewText(R.id.tvMins, "min away");
                else views.setTextViewText(R.id.tvMins, "mins away");

                Log.v("DEBUG",routeShortName);
                Log.v("DEBUG",stopName);

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WidgetConfigurationActivity.this);

                if(routeShortName.equals("")) {
                    Log.v("DEBUG", "routeName" + mAppWidgetId);
                    prefs.edit().putString("routeName" + mAppWidgetId,routeLongName).commit();
                    views.setTextViewText(R.id.tvRoute, routeLongName);
                } else {
                    Log.v("DEBUG", "routeName" + mAppWidgetId);
                    prefs.edit().putString("routeName" + mAppWidgetId,routeShortName).commit();
                    views.setTextViewText(R.id.tvRoute, routeShortName);
                }

                prefs.edit().putString("stopName" + mAppWidgetId, stopName).commit();
                views.setTextViewText(R.id.tvStop, stopName);



                Intent clickIntent = new Intent(getBaseContext(), TranslocWidgetProvider.class);

                clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(),mAppWidgetId,clickIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.rlWidgetLayout, pendingIntent);

                // Tell the AppWidgetManager to perform an update on the app widget
                appWidgetManager.updateAppWidget(mAppWidgetId, views);
                Log.v("Config activity","mappwidgetid: " + mAppWidgetId);

                // Return RESULT_OK from this activity
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);

                dialog.dismiss();
                finish();

            }

        }



    }





}


