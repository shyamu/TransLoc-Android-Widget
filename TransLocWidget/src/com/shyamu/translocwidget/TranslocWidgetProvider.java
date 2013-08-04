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

import java.io.IOException;
import java.util.Date;

public class TransLocWidgetProvider extends AppWidgetProvider {

    private AppWidgetManager appWidgetManager;
    private RemoteViews newView = null;

    private static final String TAG = "WidgetProvider";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "in onReceive with intent action: " + intent.getAction());

        newView = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        appWidgetManager = AppWidgetManager.getInstance(context);

        if (intent.getAction() == null) {
            // action is from tap of widget
            Bundle extras = intent.getExtras();
            if (extras != null) {
                // widget Id is Id of tapped widget
                int receivedWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
                new getJsonResponse(context, receivedWidgetId).execute();

            }
        } else if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // device has been rebooted, update all existing widgets
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), TransLocWidgetProvider.class.getName());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
            for(int i = 0; i<appWidgetIds.length; i++)
            {
                new getJsonResponse(context,appWidgetIds[i]).execute();
            }
        } else {
            // do nothing
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        Log.v(TAG, "in onUpdate with " + appWidgetIds.length + " existing widgets");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean configComplete = prefs.getBoolean("configComplete", false);
        if(configComplete) {
            for(int i = 0; i<appWidgetIds.length; i++)
            {
                new getJsonResponse(context,appWidgetIds[i]).execute();
            }
        }


    }

    private class getJsonResponse extends AsyncTask<Void, Void, TransLocArrivalEstimates> {

        private int minutes = -1;
        private Context context;
        private int widgetId;

        private SharedPreferences prefs;

        public getJsonResponse(Context context, int widgetId) {
            this.context = context;
            this.widgetId = widgetId;
            prefs = PreferenceManager.getDefaultSharedPreferences(context);
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(context,"Updating...",Toast.LENGTH_SHORT).show();
            Log.v(TAG, "currently updating widget with widgetID: " + widgetId);
        }

        @Override
        protected TransLocArrivalEstimates doInBackground(Void... voids) {
            String url = prefs.getString("url" + widgetId, "");

            if (url.equals("")) Log.e("ERROR widgetprovider", "URL is empty");
            Log.v(TAG,"widget update url: " + url);

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
                Log.e(TAG, "no arrival times error");
                newView.setTextViewText(R.id.tvRemainingTime, "--");
                Toast.makeText(context, "No arrival times found. Please try again later", Toast.LENGTH_LONG).show();
            } else {
                TransLocArrivalEstimate arrivalEstimate = arrivalEstimatesList.data.get(0);
                TransLocArrival arrival = arrivalEstimate.arrivals.get(0);
                currentTimeUTC = arrivalEstimatesList.generatedOn;
                arrivalTimeUTC = arrival.arrivalAt;
                Log.v(TAG, "current time: " + currentTimeUTC + " ... " + "arrival time: " + arrivalTimeUTC);
                minutes = Utils.getMinutesBetweenTimes(currentTimeUTC, arrivalTimeUTC);

                // show toasts and update widget view
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

            String routeName = prefs.getString("routeName" + widgetId, "error: route name not found");
            String stopName = prefs.getString("stopName" + widgetId, "error: stop name not found");
            newView.setTextViewText(R.id.tvRoute, routeName);
            newView.setTextViewText(R.id.tvStop, stopName);

            // reset pendingintent for widget tap
            Intent clickIntent = new Intent(context, TransLocWidgetProvider.class);
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, widgetId, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            newView.setOnClickPendingIntent(R.id.rlWidgetLayout, pendingIntent);

            Log.v(TAG, "about to call updateAppWidget with widgetId: " + widgetId);
            appWidgetManager.updateAppWidget(widgetId, newView);
        }
    }
}