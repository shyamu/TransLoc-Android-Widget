package com.shyamu.translocwidget;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shyamu.translocwidget.bl.ArrivalTimeCalculator;
import com.shyamu.translocwidget.rest.service.ServiceGenerator;
import com.shyamu.translocwidget.rest.service.TransLocClient;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.DateTime;
import org.joda.time.Minutes;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;

import static com.shyamu.translocwidget.Utils.TransLocDataType.ARRIVAL;


public class Utils {

    public enum TransLocDataType {
        AGENCY, ROUTE, STOP, ARRIVAL
    }

    public static final String GET_AGENCIES_URL = "https://transloc-api-1-2.p.mashape.com/agencies.json";
    public static final String GET_ROUTES_URL = "https://transloc-api-1-2.p.mashape.com/routes.json?agencies=";
    public static final String GET_STOPS_URL = "https://transloc-api-1-2.p.mashape.com/stops.json?agencies=";
    public static final String GET_ARRIVAL_ESTIMATES_URL = "https://transloc-api-1-2.p.mashape.com/arrival-estimates.json?agencies=";
    public static final String BASE_URL = "https://transloc-api-1-2.p.mashape.com";
    public static final String FILE_NAME = "WidgetList";
    public static final String TAP_ON_WIDGET_ACTION = "TAPPED_ON_WIDGET";

    protected static void showAlertDialog(Context context, String title, String message) {
        new AlertDialog.Builder( context )
                .setTitle( title )
                .setMessage( message )
                .setNeutralButton( "Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("AlertDialog", "Neutral");
                    }
                })
                .show();
    }

    public static int getMinutesBetweenTimes(DateTime currentTime, DateTime futureTime)
    {
        return Minutes.minutesBetween(currentTime,futureTime).getMinutes();
    }

    protected static String getJsonResponse(String url, String key) throws Exception {
        String response = "";
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("X-Mashape-Authorization",key);
        try {
            HttpResponse execute = client.execute(httpGet);
            int statusCode = execute.getStatusLine().getStatusCode();
            if(statusCode != HttpStatus.SC_OK) {
                Log.e("Utils", "error in HTTP");
                Log.e("Utils", "Did you rememember to put the API_KEY from mashape.com into res/values/strings.xml? :) ");
                throw new Exception();
            } else {
                InputStream content = execute.getEntity().getContent();
                BufferedReader buffer = new BufferedReader(
                        new InputStreamReader(content));
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    response += s;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(response == "")
        {
            throw new Exception();
        } else {
            return response;
        }
    }

    protected static void writeData(Context context, String data) throws IOException {
        FileOutputStream fOut = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
        OutputStreamWriter osw = new OutputStreamWriter(fOut);
        osw.write(data);
        osw.flush();
        osw.close();
    }

    protected static String readSavedData(Context context) throws IOException {
        StringBuffer datax = new StringBuffer("");
        FileInputStream fin = context.openFileInput(FILE_NAME);
        InputStreamReader isr = new InputStreamReader(fin);
        BufferedReader reader = new BufferedReader(isr);

        String readString = reader.readLine();
        while (readString != null) {
            datax.append(readString);
            readString = reader.readLine();
        }
        isr.close();
        return datax.toString();
    }

    public static ArrayList<ArrivalTimeWidget> getArrivalTimeWidgetsFromStorage(Context context) throws FileNotFoundException, IOException {
        String widgetListJsonStr = Utils.readSavedData(context);
        return new Gson().fromJson(widgetListJsonStr, new TypeToken<ArrayList<ArrivalTimeWidget>>() {
        }.getType());
    }

    public static RemoteViews createRemoteViews(Context context, ArrivalTimeWidget atw, int appWidgetId) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        int minutesUntilArrival = atw.getMinutesUntilArrival();
        remoteViews.setTextViewText(R.id.tvRemainingTime, Integer.toString(minutesUntilArrival));
        if (minutesUntilArrival < 1) remoteViews.setTextViewText(R.id.tvRemainingTime, "<1");
        if (minutesUntilArrival < 2) remoteViews.setTextViewText(R.id.tvMins, "min away");
        else remoteViews.setTextViewText(R.id.tvMins, "mins away");

        // Set on tap pending intent
        Intent widgetTapIntent = new Intent(context, WidgetProvider.class);
        widgetTapIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        widgetTapIntent.setAction(Utils.TAP_ON_WIDGET_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, widgetTapIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.rlWidgetLayout, pendingIntent);

        // set colors
        remoteViews.setInt(R.id.rlWidgetLayout, "setBackgroundColor", atw.getBackgroundColor());
        remoteViews.setTextColor(R.id.tvRoute, atw.getTextColor());
        remoteViews.setTextColor(R.id.tvStop, atw.getTextColor());
        remoteViews.setTextColor(R.id.tvRemainingTime, atw.getTextColor());
        remoteViews.setTextColor(R.id.tvMins, atw.getTextColor());
        // set text content
        remoteViews.setTextViewText(R.id.tvRoute, atw.getRouteName());
        remoteViews.setTextViewText(R.id.tvStop, atw.getStopName());

        return remoteViews;
    }

}


