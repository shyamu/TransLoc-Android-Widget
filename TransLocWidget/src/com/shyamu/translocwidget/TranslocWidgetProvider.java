package com.shyamu.translocwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
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


    AppWidgetManager appWidgetManager;
    RemoteViews newView = null;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.v("DEBUG", "in onReceive");

        newView = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        appWidgetManager = AppWidgetManager.getInstance(context);

        Log.v("DEBUG", "intent action = " + intent.getAction());
        if (intent.getAction() == null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Log.v("DEBUG", "in onReceive after widget tap");

                int receivedWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
                Log.v("onReceive provider", "widgetId = " + receivedWidgetId);
                // do something for the widget that has appWidgetId = widgetId

                Log.v("DEBUG", "about to call getJsonResponse from onReceive with widgetid: " + receivedWidgetId);
                getJsonResponse task = new getJsonResponse(context, receivedWidgetId);
                task.execute();

            }
        } else if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.v("DEBUG", "in onReceive: recreating onReboot");
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), TranslocWidgetProvider.class.getName());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
            for(int i = 0; i<appWidgetIds.length; i++)
            {
                Log.v("DEBUG", "about to call getJsonResponse from onRecieve onReboot with widget id: " + appWidgetIds[i]);
                getJsonResponse task = new getJsonResponse(context,appWidgetIds[i]);
                task.execute();
            }

        } else {
            super.onReceive(context, intent);
            Log.v("DEBUG", "in onReceive: doing nothing");
            // do nothing
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        Log.v("DEBUG", " in onUpdate");
        Log.v("DEBUG", "number of widgetID: " + appWidgetIds.length);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean configComplete = prefs.getBoolean("configComplete", false);
        Log.v("DEBUG", "configComplete: " + configComplete);
        if(configComplete) {
            for(int i = 0; i<appWidgetIds.length; i++)
            {
                Log.v("DEBUG", "about to call getJsonResponse from onUpdate with widget id: " + appWidgetIds[i]);
                getJsonResponse task = new getJsonResponse(context,appWidgetIds[i]);
                task.execute();
            }
        } else {
            // do nothing
        }


    }



    private class getJsonResponse extends AsyncTask<Void, Void, TransLocArrivalEstimates> {

        int minutes = -1;
        private Context context;
        private int widgetId;


        public getJsonResponse(Context context, int widgetId) {
            this.context = context;
            this.widgetId = widgetId;
        }

        @Override
        protected void onPreExecute() {

            Toast.makeText(context,"Updating...",Toast.LENGTH_SHORT).show();
            Log.v("DEBUG", "updating widget with widgetID: " + widgetId);
        }

        @Override
        protected TransLocArrivalEstimates doInBackground(Void... voids) {

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
            String routeName = prefs.getString("routeName" + widgetId, "error: route name not found");
            String stopName = prefs.getString("stopName" + widgetId, "error: stop name not found");
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