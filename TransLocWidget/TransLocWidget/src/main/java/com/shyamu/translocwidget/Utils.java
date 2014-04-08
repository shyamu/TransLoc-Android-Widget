package com.shyamu.translocwidget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.DateTime;
import org.joda.time.Minutes;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;


public class Utils {

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

    protected static int getMinutesBetweenTimes(Date currentTime, Date futureTime)
    {
        DateTime start = new DateTime(currentTime);
        DateTime end = new DateTime(futureTime);
        return Minutes.minutesBetween(start,end).getMinutes();
    }

    protected static String getJsonResponse(String url) throws Exception {

        String response = "";
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse execute = client.execute(httpGet);
            int statusCode = execute.getStatusLine().getStatusCode();
            if(statusCode != HttpStatus.SC_OK) {
                Log.e("Utils", "error in HTTP");
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


}


