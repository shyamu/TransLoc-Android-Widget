package com.shyamu.translocwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.shyamu.translocwidget.bl.ArrivalTimeCalculator;

/**
 * Created by Shyamal on 3/16/2015.
 */
public class WidgetProvider extends AppWidgetProvider {

    private static final String TAG = "WidgetProvider";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(TAG, "in onReceive with intent action: " + intent.getAction());
        if(intent.getAction().equals(Utils.TAP_ON_WIDGET_ACTION)) {
            Log.d(TAG, "Tapped on Widget");
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        Log.d(TAG, "in onUpdate with " + appWidgetIds.length + " app widget Ids");
        for (int appWidgetId: appWidgetIds) {
            Bundle bundle = appWidgetManager.getAppWidgetOptions(appWidgetId);
            ArrivalTimeWidget atw = (ArrivalTimeWidget) bundle.getSerializable("atw");
            if(atw != null) {
                Log.d(TAG, atw.toString());
                ArrivalTimeCalculator arrivalTimeCalculator = new ArrivalTimeCalculator(context, atw);
                atw = arrivalTimeCalculator.getArrivalTimeWidgetWithUpdatedTime();
                RemoteViews remoteViews = Utils.createRemoteViews(context, atw, appWidgetId);
                Log.v(TAG, "about to call updateAppWidget with widgetId: " + appWidgetId);
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            } else {
                Log.e(TAG, "bundle not found for atw widgetId: " + appWidgetId);
            }

        }

    }




    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}
