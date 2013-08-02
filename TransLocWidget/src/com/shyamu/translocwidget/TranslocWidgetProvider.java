package com.shyamu.translocwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shyamu.translocwidget.TransLocJSON.TransLocArrival;
import com.shyamu.translocwidget.TransLocJSON.TransLocArrivalEstimate;
import com.shyamu.translocwidget.TransLocJSON.TransLocArrivalEstimates;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

public class TranslocWidgetProvider extends AppWidgetProvider {

    int widgetId = -1;
    AppWidgetManager appWidgetManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.v("DEBUG", "in onRecieve");

        if (intent.getAction() == null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Log.v("DEBUG", "extras!=null");
                appWidgetManager = AppWidgetManager.getInstance(context);
                widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
                Log.v("onReceive provider", "widgetId = " + widgetId);
                // do something for the widget that has appWidgetId = widgetId

                Log.v("DEBUG", "about to call getJsonResponse");
                getJsonResponse task = new getJsonResponse(context);
                task.execute();

            }
        } else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        Log.v("DEBUG", " in onUpdate");
        Log.v("DEBUG", "about to call getJsonResponse");
        if(widgetId != -1)
        {
            getJsonResponse task = new getJsonResponse(context);
            task.execute();
        } else {
            // do nothing
        }

    }

    private class getJsonResponse extends AsyncTask<Void, Void, TransLocArrivalEstimates> {

        int minutes = -1;
        RemoteViews newView;
        private Context context;


        public getJsonResponse(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {

            Toast.makeText(context,"Updating...",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected TransLocArrivalEstimates doInBackground(Void... voids) {

            newView = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String url = prefs.getString("url" + widgetId, "");

            if (url.equals("")) Log.e("ERROR widgetprovider", "URL is empty");

            Log.v("DEBUG", url);
            try {
                return new ObjectMapper().readValue(Utils.getJsonResponse(url), TransLocArrivalEstimates.class);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPostExecute(TransLocArrivalEstimates arrivalEstimatesList) {

            Date currentTimeUTC;
            Date arrivalTimeUTC;

            if (arrivalEstimatesList == null || arrivalEstimatesList.data.isEmpty()) {
                Log.v("DEBUG", "no arrival times error");
                newView.setTextViewText(R.id.tvRemainingTime, "--");
                Toast.makeText(context, "No arrival times found. Please try again later", Toast.LENGTH_LONG).show();
            } else {
                TransLocArrivalEstimate arrivalEstimate = arrivalEstimatesList.data.get(0);
                TransLocArrival arrival = arrivalEstimate.arrivals.get(0);
                currentTimeUTC = arrivalEstimatesList.generatedOn;
                arrivalTimeUTC = arrival.arrivalAt;
                Log.v("DEBUG", "current time: " + currentTimeUTC + " ... " + "arrival time: " + arrivalTimeUTC);
                minutes = Utils.getMinutesBetweenTimes(currentTimeUTC, arrivalTimeUTC);

                if (minutes < 1) {
                    Toast.makeText(context, "Next bus is less than 1 minute away!", Toast.LENGTH_LONG).show();
                    newView.setTextViewText(R.id.tvRemainingTime, "<1");
                    newView.setTextViewText(R.id.tvMins, "min away");
                } else if (minutes == 1) {
                    Toast.makeText(context, "Next bus is 1 minute away!", Toast.LENGTH_LONG).show();
                    newView.setTextViewText(R.id.tvRemainingTime, "1");
                    newView.setTextViewText(R.id.tvMins, "min away");
                } else {
                    Toast.makeText(context, "Next bus is " + minutes + " minutes away", Toast.LENGTH_LONG).show();
                    newView.setTextViewText(R.id.tvRemainingTime, Integer.toString(minutes));
                    newView.setTextViewText(R.id.tvMins, "mins away");
                }


            }

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            Log.v("DEBUG", "routeName" + widgetId);
            String routeName = prefs.getString("routeName" + widgetId, "error: route name");
            String stopName = prefs.getString("stopName" + widgetId, "error: stop name");
            Log.v("DEBUG", routeName);
            Log.v("DEBUG", stopName);
            newView.setTextViewText(R.id.tvRoute, routeName);
            newView.setTextViewText(R.id.tvStop, stopName);

            Intent clickIntent = new Intent(context, TranslocWidgetProvider.class);
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, widgetId, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            newView.setOnClickPendingIntent(R.id.rlWidgetLayout, pendingIntent);

            Log.v("DEBUG", "about to call updateAppWidget with widgetId: " + widgetId);

            appWidgetManager.updateAppWidget(widgetId, newView);
        }


    }


}