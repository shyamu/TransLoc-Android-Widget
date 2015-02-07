package com.shyamu.translocwidget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
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
    private int widgetSize = 4;

    private static final String TAG = "WidgetProvider";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "in onReceive with intent action: " + intent.getAction());

       // newView = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        appWidgetManager = AppWidgetManager.getInstance(context);
        String action = intent.getAction();
        if (action == null) {
            // action is from tap of widget
            Log.v(TAG, "in onReceive with intent action: null");
            Bundle extras = intent.getExtras();
            if (extras != null) {
                // widget Id is Id of tapped widget
                int receivedWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
                new getJsonResponse(context, receivedWidgetId, false).execute();
            }
        } else if(action.equals("android.intent.action.BOOT_COMPLETED") || action.equals("android.appwidget.action.APPWIDGET_UPDATE")) {
            // device has been rebooted or app has been updated, update all existing widgets
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), TransLocWidgetProvider.class.getName());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
            for (int appWidgetId : appWidgetIds) {
                new getJsonResponse(context, appWidgetId, true).execute();
            }
        } else if(action.equals("android.appwidget.action.APPWIDGET_UPDATE_OPTIONS")) {
            // widget is resized
            Bundle extras = intent.getExtras();
            if (extras != null) {
                // widget Id is Id of tapped widget
                int receivedWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
                new getJsonResponse(context, receivedWidgetId, false).execute();
            }
        } else {
            // do nothing
            Log.v(TAG, "did nothing in onRecieve");
        }
    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean configComplete = prefs.getBoolean("configComplete", false);
        if(configComplete) {
            for (int appWidgetId : appWidgetIds) {
                new getJsonResponse(context, appWidgetId, false).execute();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void drawWidgetForJellyBean(Context context, int appWidgetId) {
        Log.v(TAG, "in drawWidget");
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        Bundle widgetOptions = manager.getAppWidgetOptions(appWidgetId);

        if(widgetOptions != null) {
            int minWidthDp = widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            int minHeightDp = widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
            if(minWidthDp <= 72) widgetSize = 1;
            else if(minWidthDp <= 160) widgetSize = 2;
            else if(minWidthDp <= 248) widgetSize = 3;
            else widgetSize = 4;
        } else {
            Log.e(TAG, "widget options is null");
        }

        newView = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        newView.removeAllViews(R.id.rlWidgetLayout);

        if(widgetSize == 3) {
            RemoteViews threeWidget = new RemoteViews(context.getPackageName(), R.layout.widget_layout_three);
            newView.addView(R.id.rlWidgetLayout,threeWidget);
        } else if (widgetSize == 2) {
            RemoteViews twoWidget = new RemoteViews(context.getPackageName(), R.layout.widget_layout_two);
            newView.addView(R.id.rlWidgetLayout,twoWidget);
        } else if (widgetSize == 1) {
            RemoteViews oneWidget = new RemoteViews(context.getPackageName(), R.layout.widget_layout_one);
            newView.addView(R.id.rlWidgetLayout,oneWidget);
        } else {
            Log.v(TAG,"widget size not found");
            RemoteViews fourWidget = new RemoteViews(context.getPackageName(), R.layout.widget_layout_three);
            newView.addView(R.id.rlWidgetLayout,fourWidget);
        }
    }

    private class getJsonResponse extends AsyncTask<Void, Void, TransLocArrivalEstimates> {
        private int minutes = -1;
        private Context context;
        private int widgetId;
        private boolean onReboot;

        private SharedPreferences prefs;

        public getJsonResponse(Context context, int widgetId, boolean onReboot) {
            this.context = context;
            this.widgetId = widgetId;
            prefs = PreferenceManager.getDefaultSharedPreferences(context);
            this.onReboot = onReboot;
        }

        @Override
        protected void onPreExecute() {
            //if(!onReboot) Toast.makeText(context,"Updating...",Toast.LENGTH_SHORT).show();
            Log.v(TAG, "currently updating widget with widgetID: " + widgetId);
        }

        @Override
        protected TransLocArrivalEstimates doInBackground(Void... voids) {
            String url = prefs.getString("url" + widgetId, "");

            if (url.equals("")) Log.e("ERROR widgetprovider", "URL is empty");
            //Log.v(TAG,"widget update url: " + url);

            try {
                if(onReboot) return null;
                else return new ObjectMapper().readValue(Utils.getJsonResponse(url, context.getString(R.string.mashape_key)), TransLocArrivalEstimates.class);
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

            // for resizable widgets
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                drawWidgetForJellyBean(context, widgetId);
            } else {
                newView = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            }

            Date currentTimeUTC;
            Date arrivalTimeUTC;

            if (arrivalEstimatesList == null || arrivalEstimatesList.data.isEmpty()) {
               // Log.e(TAG, "no arrival times error");
                newView.setTextViewText(R.id.tvRemainingTime, "--");
                if(!onReboot) Toast.makeText(context, "No arrival times found. Please try again later", Toast.LENGTH_LONG).show();
            } else {
                TransLocArrivalEstimate arrivalEstimate = arrivalEstimatesList.data.get(0);
                TransLocArrival arrival = arrivalEstimate.arrivals.get(0);
                currentTimeUTC = arrivalEstimatesList.generatedOn;
                arrivalTimeUTC = arrival.arrivalAt;
              //  Log.v(TAG, "current time: " + currentTimeUTC + " ... " + "arrival time: " + arrivalTimeUTC);
                minutes = Utils.getMinutesBetweenTimes(currentTimeUTC, arrivalTimeUTC);

                // show toasts and update widget view
                if (minutes < 1) {
                   // if(!onReboot) Toast.makeText(context, "Next bus is less than 1 minute away!", Toast.LENGTH_SHORT).show();
                    newView.setTextViewText(R.id.tvRemainingTime, "<1");
                    if(widgetSize >= 3) newView.setTextViewText(R.id.tvMins, "min away");
                } else if (minutes == 1) {
                   // if(!onReboot) Toast.makeText(context, "Next bus is 1 minute away!", Toast.LENGTH_SHORT).show();
                    newView.setTextViewText(R.id.tvRemainingTime, "1");
                    if(widgetSize >= 3) newView.setTextViewText(R.id.tvMins, "min away");
                } else {
                   // if(!onReboot) Toast.makeText(context, "Next bus is " + minutes + " minutes away", Toast.LENGTH_SHORT).show();
                    newView.setTextViewText(R.id.tvRemainingTime, Integer.toString(minutes));
                    if(widgetSize >= 3) newView.setTextViewText(R.id.tvMins, "mins away");
                }
            }

            String routeName = prefs.getString("routeName" + widgetId, "error: route name not found");
            String stopName = prefs.getString("stopName" + widgetId, "error: stop name not found");

            // reset colors for widget
            int textColor = prefs.getInt("textColor-" + widgetId, -1);
            int backgroundColor = prefs.getInt("backgroundColor-" + widgetId, -1);
            newView.setInt(R.id.rlWidgetLayout, "setBackgroundColor", backgroundColor);
            newView.setTextColor(R.id.tvRemainingTime, textColor);

            if(widgetSize >= 3) {
                newView.setTextViewText(R.id.tvRoute, routeName);
                newView.setTextViewText(R.id.tvStop, stopName);
                newView.setTextColor(R.id.tvRoute, textColor);
                newView.setTextColor(R.id.tvStop, textColor);
                newView.setTextColor(R.id.tvMins, textColor);
            } else if(widgetSize == 2) {
                newView.setTextViewText(R.id.tvRoute, routeName);
                newView.setTextColor(R.id.tvRoute, textColor);
                newView.setTextViewText(R.id.tvStop, stopName);
                newView.setTextColor(R.id.tvStop, textColor);
            } else if(widgetSize == 1) {
                newView.setTextViewText(R.id.tvRouteAndStop, routeName + " - " + stopName);
                newView.setTextColor(R.id.tvRouteAndStop, textColor);
            }

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


