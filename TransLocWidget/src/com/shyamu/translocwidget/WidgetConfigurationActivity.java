package com.shyamu.translocwidget;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Spinner;

public class WidgetConfigurationActivity extends Activity {
    private int mAppWidgetId = 0;

    private String agencyId = "";
    private String routeId = "";
    private String stopId = "";

    private int agencyPosition = -1;
    private int routePosition = -1;
    private int stopPosition = -1;



    Spinner sSelectAgency, sSelectRoute, sSelectStop;
    Button bReset, bMakeWidget;

    ArrayList<String> agencyLongNameArray = new ArrayList<String>();
    ArrayList<String> agencyShortNameArray = new ArrayList<String>();
    ArrayList<String> agencyIdArray = new ArrayList<String>();

    ArrayList<String> routeLongNameArray = new ArrayList<String>();
    ArrayList<String> routeShortNameArray = new ArrayList<String>();
    ArrayList<String> routeIdArray = new ArrayList<String>();

    ArrayList<String> stopNameArray = new ArrayList<String>();
    ArrayList<String> stopShortNameArray = new ArrayList<String>();
    ArrayList<String> stopIdArray = new ArrayList<String>();

    ArrayAdapter<String> agencyArrayAdapter;
    ArrayAdapter<String> routeArrayAdapter;
    ArrayAdapter<String> stopArrayAdapter;

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

        // Make agency selected listener
        AdapterView.OnItemSelectedListener agencySelectedListener = new AgencySpinnerActivity();
        sSelectAgency.setOnItemSelectedListener(agencySelectedListener);

        // Make route selected listener
        AdapterView.OnItemSelectedListener routeSelectedListener = new RouteSpinnerActivity();
        sSelectRoute.setOnItemSelectedListener(routeSelectedListener);

        // Make stop selected listener
        AdapterView.OnItemSelectedListener stopSelectedListener = new StopSpinnerActivity();
        sSelectStop.setOnItemSelectedListener(stopSelectedListener);

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


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {

            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // Setting the click listener on the buttons
        bReset.setOnClickListener(setResetClickedListener);
        bMakeWidget.setOnClickListener(setMakeWidgetClickedListener);
    }

    private void doReset() {
        PopulateAgenciesTask task = new PopulateAgenciesTask();
        task.execute();

        agencyArrayAdapter.notifyDataSetChanged();
        routeArrayAdapter.notifyDataSetChanged();
        stopArrayAdapter.notifyDataSetChanged();


    }

    private void doMakeWidget() {
        Log.v("TESTING","Now testing variables");

        Log.v("TESTING","agency ID: " + agencyId);
        Log.v("TESTING","agency long name:" + agencyLongNameArray.get(agencyPosition));
        Log.v("TESTING","agency short name:" + agencyShortNameArray.get(agencyPosition));
        Log.v("TESTING","route ID: + " + routeId);
        Log.v("TESTING","route long name:" + routeLongNameArray.get(routePosition));
        Log.v("TESTING","route short name:" + routeShortNameArray.get(routePosition));
        Log.v("TESTING","stop ID: + " + stopIdArray.get(stopPosition));
        Log.v("TESTING","stop name:" + stopNameArray.get(stopPosition));

        // Get arrival time
        PopulateArrivalTask task = new PopulateArrivalTask();
        task.execute();




    }

    private class PopulateAgenciesTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(WidgetConfigurationActivity.this,"Loading Agencies","Please Wait...");
        }

        protected Void doInBackground(Void... voids) {

            String response = getJsonResponse("http://api.transloc.com/1.1/agencies.json");

            try {
                JSONObject jObject = new JSONObject(response);
                JSONArray jArray = jObject.getJSONArray("data");
                for (int i = 0; i < jArray.length(); i++) {
                    agencyLongNameArray.add(jArray.getJSONObject(i).getString(
                            "long_name"));
                    agencyShortNameArray.add(jArray.getJSONObject(i).getString(
                            "short_name"));
                    agencyIdArray.add(jArray.getJSONObject(i).getString(
                            "agency_id"));
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e("JSON", "ERROR in getting JSON data");
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            agencyArrayAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, agencyLongNameArray);
            sSelectAgency.setAdapter(agencyArrayAdapter);

            dialog.dismiss();
        }

    }

    private class PopulateRoutesTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {

            dialog = ProgressDialog.show(WidgetConfigurationActivity.this,"Loading Routes","Please Wait...");

            agencyPosition = sSelectAgency.getSelectedItemPosition();
            agencyId = agencyIdArray.get(agencyPosition);
            Log.v("DEBUG", "Selected agency ID is " + agencyId);


            routeLongNameArray.clear();
            routeIdArray.clear();
            routeShortNameArray.clear();
            stopShortNameArray.clear();
            stopNameArray.clear();
            stopIdArray.clear();
        }

        protected Void doInBackground(Void... voids) {

            String response = getJsonResponse("http://api.transloc.com/1.1/routes.json?agencies=" + agencyId);
            try {
                JSONObject jObject = new JSONObject(response);
                JSONObject jObjectData = jObject.getJSONObject("data");
                JSONArray jArrayAgency = jObjectData.getJSONArray(agencyId);
                for (int i = 0; i < jArrayAgency.length(); i++) {
                    routeLongNameArray.add(jArrayAgency.getJSONObject(i).getString(
                            "long_name"));
                    routeShortNameArray.add(jArrayAgency.getJSONObject(i).getString(
                            "short_name"));
                    routeIdArray.add(jArrayAgency.getJSONObject(i).getString(
                            "route_id"));
                }


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e("JSON", "ERROR in getting JSON data");
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            routeArrayAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, routeLongNameArray);
            sSelectRoute.setAdapter(routeArrayAdapter);

            dialog.dismiss();

            if(routeArrayAdapter.isEmpty()) {
                showAlertDialog("Error - No Routes Available", "No routes are currently available for the agency you have selected. Please try again later when buses are running.");
            }
        }

    }

    private class PopulateStopsTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {

            dialog = ProgressDialog.show(WidgetConfigurationActivity.this,"Loading Stops","Please Wait...");

            routePosition = sSelectRoute.getSelectedItemPosition();
            routeId = routeIdArray.get(routePosition);
            Log.v("DEBUG", "Selected route ID is " + routeId);


            stopIdArray.clear();
            stopNameArray.clear();
            stopShortNameArray.clear();

        }

        protected Void doInBackground(Void... voids) {

            String response = getJsonResponse("http://api.transloc.com/1.1/stops.json?agencies=" + agencyId);

            try {
                JSONObject jObject = new JSONObject(response);
                JSONArray jArrayData = jObject.getJSONArray("data");
                for(int i = 0; i < jArrayData.length(); i++)
                {
                    JSONObject jObjectStop = jArrayData.getJSONObject(i);
                    JSONArray jArrayStopRoutes = jObjectStop.getJSONArray("routes");
                    for(int j = 0; j < jArrayStopRoutes.length(); j++)
                    {
                        if(jArrayStopRoutes.get(j).equals(routeId)) {
                            stopNameArray.add((String) jObjectStop.get("name"));
                            stopIdArray.add((String) jObjectStop.get("stop_id"));
                            break;
                        }
                    }

                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e("JSON", "ERROR in getting JSON data");
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            stopArrayAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, stopNameArray);
            sSelectStop.setAdapter(stopArrayAdapter);

            dialog.dismiss();

            if(stopArrayAdapter.isEmpty()) {
                showAlertDialog("Error - No Stops Available", "No stops are currently available for the route you have selected. Please try again later when buses are running.");
            }
        }



    }

    private class PopulateArrivalTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;
        int errorCode = -1;

        private String currentTimeUTC = "";
        private String arrivalTimeUTC = "";

        private int minutes = -1;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(WidgetConfigurationActivity.this,"Making Widget","Please Wait...");

            Log.v("DEBUG", "Getting arrival times for following info");
            Log.v("DEBUG", "agency id: " + agencyId);
            Log.v("DEBUG", "route id:" + routeId);
            Log.v("DEBUG", "stop id:" + stopId);


        }

        protected Void doInBackground(Void... voids) {

            String response = getJsonResponse("http://api.transloc.com/1.1/arrival-estimates.json?agencies=" + agencyId + "&routes=" + routeId + "&stops=" + stopId);
            try {
                JSONObject jObject = new JSONObject(response);
                currentTimeUTC = jObject.getString("generated_on");
                JSONArray jArrayData = jObject.getJSONArray("data");
                JSONObject jObjectArrayData = jArrayData.getJSONObject(0);
                JSONArray jArrayArrivals = jObjectArrayData.getJSONArray("arrivals");
                JSONObject jObjectArrayArrivals = jArrayArrivals.getJSONObject(0);
                arrivalTimeUTC = jObjectArrayArrivals.getString("arrival_at");
                errorCode = 0;

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                errorCode = 1;
                Log.e("JSON", "ERROR in getting JSON data");
            }
            if(errorCode == 0)  minutes = getMinutesBetweenTimes(currentTimeUTC,arrivalTimeUTC);


            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if(errorCode == 0) {
                Intent intent = new Intent(getBaseContext(), WidgetConfigurationActivity.class);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                intent.setData(Uri.parse("tel:/" + (int) System.currentTimeMillis()));
                // Creating a pending intent, which will be invoked when the user
                // clicks on the widget
                PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);

                // Getting an instance of WidgetManager
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getBaseContext());

                // Instantiating the class RemoteViews with widget_layout
                RemoteViews views = new RemoteViews(getBaseContext().getPackageName(), R.layout.widget_layout);

                //Set the time remaining of the widget
                //views.setInt(R.id.widget_aclock, "setBackgroundColor", color);
                views.setTextViewText(R.id.tvRemainingTime, Integer.toString(minutes));
                Log.v("DEBUG",routeShortNameArray.get(routePosition));
                Log.v("DEBUG",stopNameArray.get(stopPosition));

                if(routeShortNameArray.get(routePosition).equals("")) {
                    views.setTextViewText(R.id.tvRoute, routeLongNameArray.get(routePosition));
                } else {
                    views.setTextViewText(R.id.tvRoute, routeShortNameArray.get(routePosition));
                }

                views.setTextViewText(R.id.tvStop, stopNameArray.get(stopPosition));

                //  Attach an on-click listener to the time to update when clicked
                views.setOnClickPendingIntent(R.id.tvRemainingTime, pendingIntent);

                // Tell the AppWidgetManager to perform an update on the app widget
                appWidgetManager.updateAppWidget(mAppWidgetId, views);

                // Return RESULT_OK from this activity
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);

                dialog.dismiss();
                finish();
            } else if (errorCode == 1) {
                dialog.dismiss();
                //Show alert dialog
                showAlertDialog("Error - No Arrival Times","No arrival times are currently available for the route and stop you have selected. Please try again later when buses are running.");
            }
        }



    }

    private void showAlertDialog(String title, String message) {
        new AlertDialog.Builder( WidgetConfigurationActivity.this )
                .setTitle( title )
                .setMessage( message )
                .setNeutralButton( "Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d( "AlertDialog", "Neutral" );
                    }
                })
                .show();
    }

    private int getMinutesBetweenTimes(String currentTime, String futureTime)
    {
        DateTime start = new DateTime(currentTime);
        DateTime end = new DateTime(futureTime);
        Log.v("DEBUG", "minutes: " + Minutes.minutesBetween(start,end).getMinutes());
        return Minutes.minutesBetween(start,end).getMinutes();
    }

    private class AgencySpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)
            sSelectRoute.setEnabled(true);

            // Get routes
            PopulateRoutesTask task = new PopulateRoutesTask();
            task.execute();

        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    }

    private class RouteSpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)
            sSelectStop.setEnabled(true);

            // Get routes
            PopulateStopsTask task = new PopulateStopsTask();
            task.execute();

        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    }

    private class StopSpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)
            sSelectStop.setEnabled(true);

            // assign variables
            stopPosition = pos;
            stopId = stopIdArray.get(stopPosition);

        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    }

    private String getJsonResponse(String url) {

        String response = "";

        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse execute = client.execute(httpGet);
            InputStream content = execute.getEntity().getContent();

            BufferedReader buffer = new BufferedReader(
                    new InputStreamReader(content));
            String s = "";
            while ((s = buffer.readLine()) != null) {
                response += s;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

}


