package com.shyamu.translocwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.shyamu.translocwidget.bl.ArrivalTimeCalculator;
import com.shyamu.translocwidget.rest.model.TransLocArrival;
import com.shyamu.translocwidget.rest.service.ServiceGenerator;
import com.shyamu.translocwidget.rest.service.TransLocClient;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.Subscriptions;

import static com.shyamu.translocwidget.Utils.TransLocDataType.ARRIVAL;

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
        // onUpdate is called when app is updated and when a new widget is added so we need to recreate remoteviews for each widget
        // There may be multiple widgets active, so update all of them
        Log.d(TAG, "in onUpdate with " + appWidgetIds.length + " app widget Ids");
        try {
            ArrayList<ArrivalTimeWidget> listOfWidgetsInStorage = Utils.getArrivalTimeWidgetsFromStorage(context);
            for (int appWidgetId: appWidgetIds) {
                ArrivalTimeWidget atw = Utils.getArrivalTimeWidgetFromWidgetId(listOfWidgetsInStorage, appWidgetId);
                if(atw != null) {
                    Log.d(TAG, atw.toString());
                    getArrivalsFromServiceAndUpdateWidgetUI(context, atw, appWidgetManager, appWidgetId);
                } else {
                    Log.e(TAG, "ArrivalTimeWidget not found for atw widgetId: " + appWidgetId);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error in getting widgets from storage in onUpdate", e);
        }

    }

    private void getArrivalsFromServiceAndUpdateWidgetUI(Context context, ArrivalTimeWidget atw, AppWidgetManager appWidgetManager, int appWidgetId) {
        TransLocClient client =
                ServiceGenerator.createService(TransLocClient.class,
                        Utils.BASE_URL,
                        context.getString(R.string.mashape_key),
                        atw.getAgencyID(),
                        ARRIVAL);
        client.arrivalEstimates(atw.getAgencyID(), atw.getRouteID(), atw.getStopID())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((arrivals) -> {
                            handleWidgetUpdate(arrivals, appWidgetManager, atw, context, appWidgetId);
                        },
                        e -> Log.e(TAG, "Error in getting list of arrival times", e)
                );
    }

    private void handleWidgetUpdate(List<TransLocArrival> arrivals, AppWidgetManager appWidgetManager, ArrivalTimeWidget atw, Context context, int appWidgetId) {
        if(arrivals != null && !arrivals.isEmpty()) {
            TransLocArrival nextArrival = arrivals.get(0);
            int minsTillArrival = getMinsUntilArrival(nextArrival);
            atw.setMinutesUntilArrival(minsTillArrival);
            Log.v(TAG, "in handleWidgetUpdate with " + minsTillArrival + " minsTillArrival for appwidgetId= " + appWidgetId);
            RemoteViews remoteViews = Utils.createRemoteViews(context, atw, appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        } else {
            Log.e(TAG, "arrivals is null or empty!");
        }
    }

    private int getMinsUntilArrival(TransLocArrival arrival) {
        DateTime currentDate = new DateTime();
        DateTime arrivalDate = new DateTime(arrival.arrivalAt);
        return Utils.getMinutesBetweenTimes(currentDate, arrivalDate);
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
