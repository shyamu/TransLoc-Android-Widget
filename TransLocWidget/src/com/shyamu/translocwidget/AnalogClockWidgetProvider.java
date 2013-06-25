package com.shyamu.translocwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
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

    int errorCode = -1;
    String currentTimeUTC = "";
    String arrivalTimeUTC = "";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
        int[] appWidgetIds) {
        Log.v("DEBUG", " in onUpdate");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String url = prefs.getString("url", "");

        RemoteViews newView = new RemoteViews(context.getPackageName(),R.layout.widget_layout);

        String response = getJsonResponse(url);
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
            newView.setTextViewText(R.id.tvRemainingTime,Integer.toString(minutes));
        } else if (errorCode == 1) {
            // no arrival times found
            newView.setTextViewText(R.id.tvRemainingTime,"--");
            // show toast
            Toast.makeText(context, "No arrival times found. Please try again later",Toast.LENGTH_LONG).show();
        }

    }

    private int getMinutesBetweenTimes(String currentTime, String futureTime)
    {
        DateTime start = new DateTime(currentTime);
        DateTime end = new DateTime(futureTime);
        Log.v("DEBUG", "minutes: " + Minutes.minutesBetween(start, end).getMinutes());
        return Minutes.minutesBetween(start,end).getMinutes();
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