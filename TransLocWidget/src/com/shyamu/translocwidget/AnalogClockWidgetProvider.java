package com.shyamu.translocwidget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AnalogClockWidgetProvider extends AppWidgetProvider {

    int widgetId;
    AppWidgetManager appWidgetManager;


    @Override
    public void onReceive(Context context, Intent intent) {

        Log.v("DEBUG", "in onRecieve");

        if (intent.getAction()==null) {
            Bundle extras = intent.getExtras();
            if(extras!=null) {
                Log.v("DEBUG", "extras!=null");
                appWidgetManager= AppWidgetManager.getInstance(context);
                widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
                Log.v("onReceive provider","widgetId = " + widgetId);
                // do something for the widget that has appWidgetId = widgetId


                getJsonResponse task = new getJsonResponse(context);
                task.execute();

            }
        }
        else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
        int[] appWidgetIds) {
        Log.v("DEBUG", " in onUpdate");



    }

    private static int getMinutesBetweenTimes(String currentTime, String futureTime)
    {
        DateTime start = new DateTime(currentTime);
        DateTime end = new DateTime(futureTime);
        Log.v("DEBUG", "minutes: " + Minutes.minutesBetween(start, end).getMinutes());
        return Minutes.minutesBetween(start,end).getMinutes();
    }



    private class getJsonResponse extends AsyncTask<Void, Void, String> {

        int errorCode = -1;
        String currentTimeUTC = "";
        String arrivalTimeUTC = "";

        private Context context;

        RemoteViews newView;


        public getJsonResponse(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(context, "Updating...",Toast.LENGTH_SHORT).show();
        }


        @Override
        protected String doInBackground(Void... voids) {




            newView = new RemoteViews(context.getPackageName(),R.layout.widget_layout);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String url = prefs.getString("url" + widgetId, "");

            if(url.equals("")) Log.e("ERROR widgetprovider", "URL is empty");

            Log.v("DEBUG", url);
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

        @Override
        protected void onPostExecute(String result) {
            String response = result;

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
            if(errorCode == 0) {
                int minutes = getMinutesBetweenTimes(currentTimeUTC,arrivalTimeUTC);
                // update remote views
                Toast.makeText(context, "Update success! Next bus is " + minutes + " minutes away.",Toast.LENGTH_LONG).show();

                newView.setTextViewText(R.id.tvRemainingTime,Integer.toString(minutes));
            } else if (errorCode == 1) {
                // no arrival times found
                newView.setTextViewText(R.id.tvRemainingTime,"--");
                // show toast
                Toast.makeText(context, "No arrival times found. Please try again later",Toast.LENGTH_LONG).show();
            }
            Log.v("DEBUG", "about to call updateAppWidget with widgetId: " + widgetId);


            appWidgetManager.updateAppWidget(widgetId, newView);
        }


    }


}