package com.shyamu.translocwidget;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class WidgetConfigurationActivity extends Activity {
    private int mAppWidgetId = 0;
    private String agencyId = "";
    String routeId = "";


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
        AdapterView.OnItemSelectedListener stopSelectedListener = new RouteSpinnerActivity();
        sSelectRoute.setOnItemSelectedListener(stopSelectedListener);


        // Populate agency spinner
        PopulateAgenciesTask task = new PopulateAgenciesTask();
        task.execute();

        // Defining a click event listener for the button "Set Color"
        OnClickListener setColorClickedListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                // colorPicker();
            }
        };

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {

            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // Setting the click listener on the "Set Color" button
        // btnSetColor.setOnClickListener(setColorClickedListener);
    }

    private class PopulateAgenciesTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... voids) {

            String response = getJsonResponse("http://api.transloc.com/1.1/agencies.json");

            try {
                JSONObject jObject = new JSONObject(response);
                JSONArray jArray = jObject.getJSONArray("data");
                for (int i = 0; i < jArray.length(); i++) {
                    Log.v("From jArray", jArray.getJSONObject(i).getString(
                            "long_name"));
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
            ArrayAdapter<String> agencyArrayAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, agencyLongNameArray);
            sSelectAgency.setAdapter(agencyArrayAdapter);
        }

    }

    private class PopulateRoutesTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            int position = sSelectAgency.getSelectedItemPosition();
            agencyId = agencyIdArray.get(position);
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


            Log.v("DEBUG", response);

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

            ArrayAdapter<String> routeArrayAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, routeLongNameArray);
            sSelectRoute.setAdapter(routeArrayAdapter);
        }

    }

    private class PopulateStopsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            int position = sSelectRoute.getSelectedItemPosition();
            routeId = routeIdArray.get(position);
            Log.v("DEBUG", "Selected route ID is " + routeId);

            routeLongNameArray.clear();
            routeIdArray.clear();
            routeShortNameArray.clear();
            stopIdArray.clear();
            stopNameArray.clear();
            stopShortNameArray.clear();

        }

        protected Void doInBackground(Void... voids) {

            String response = getJsonResponse("http://api.transloc.com/1.1/stops.json?agencies=" + agencyId);

            Log.v("DEBUG", response);

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

            ArrayAdapter<String> stopArrayAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, stopNameArray);
            sSelectRoute.setAdapter(stopArrayAdapter);
        }

    }

	/*
     * public void colorPicker() {
	 * 
	 * // initialColor is the initially-selected color to be shown in the
	 * rectangle on the left of the arrow. // for example, 0xff000000 is black,
	 * 0xff0000ff is blue. Please be aware of the initial 0xff which is the
	 * alpha. AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, 0xff0000ff,
	 * new OnAmbilWarnaListener() {
	 * 
	 * // Executes, when user click Cancel button
	 * 
	 * @Override public void onCancel(AmbilWarnaDialog dialog){ }
	 * 
	 * // Executes, when user click OK button
	 * 
	 * @Override public void onOk(AmbilWarnaDialog dialog, int color) { //
	 * Create an Intent to launch WidgetConfigurationActivity screen Intent
	 * intent = new Intent(getBaseContext(), WidgetConfigurationActivity.class);
	 * 
	 * intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
	 * 
	 * // This is needed to make this intent different from its previous intents
	 * intent.setData(Uri.parse("tel:/"+ (int)System.currentTimeMillis()));
	 * 
	 * // Creating a pending intent, which will be invoked when the user //
	 * clicks on the widget PendingIntent pendingIntent =
	 * PendingIntent.getActivity(getBaseContext(), 0, intent,
	 * PendingIntent.FLAG_UPDATE_CURRENT);
	 * 
	 * // Getting an instance of WidgetManager AppWidgetManager appWidgetManager
	 * = AppWidgetManager.getInstance(getBaseContext());
	 * 
	 * // Instantiating the class RemoteViews with widget_layout RemoteViews
	 * views = new RemoteViews(getBaseContext().getPackageName(),
	 * R.layout.widget_layout);
	 * 
	 * // Setting the background color of the widget
	 * views.setInt(R.id.widget_aclock, "setBackgroundColor", color);
	 * 
	 * // Attach an on-click listener to the clock
	 * views.setOnClickPendingIntent(R.id.widget_aclock, pendingIntent);
	 * 
	 * // Tell the AppWidgetManager to perform an update on the app widget
	 * appWidgetManager.updateAppWidget(mAppWidgetId, views);
	 * 
	 * // Return RESULT_OK from this activity Intent resultValue = new Intent();
	 * resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
	 * setResult(RESULT_OK, resultValue); finish(); } }); dialog.show();
	 * 
	 * }
	 */

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
            sSelectRoute.setEnabled(true);

            // Get routes
            PopulateStopsTask task = new PopulateStopsTask();
            task.execute();

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


