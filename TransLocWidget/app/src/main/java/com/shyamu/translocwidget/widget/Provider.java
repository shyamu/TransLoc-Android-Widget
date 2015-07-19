package com.shyamu.translocwidget.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.shyamu.translocwidget.BuildConfig;
import com.shyamu.translocwidget.R;
import com.shyamu.translocwidget.bl.ArrivalTimeWidget;
import com.shyamu.translocwidget.bl.Utils;
import com.shyamu.translocwidget.rest.model.TransLocArrival;
import com.shyamu.translocwidget.rest.service.ServiceGenerator;
import com.shyamu.translocwidget.rest.service.TransLocClient;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;

import static com.shyamu.translocwidget.bl.Utils.TransLocDataType.ARRIVAL;

/**
 * Created by Shyamal on 3/16/2015.
 */
public class Provider extends AppWidgetProvider {

    private static final String TAG = "Provider";
    private static final String TRANSLOC_API_KEY= BuildConfig.TRANSLOC_API_KEY;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(TAG, "in onReceive with intent action: " + intent.getAction());
        if(intent.getAction().equals(Utils.TAP_ON_WIDGET_ACTION)) {
            int idOfTappedWidget = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            Log.d(TAG, "Tapped on widget with widget id: " + idOfTappedWidget);
            int[] appWidgetIds = {idOfTappedWidget};
            onUpdate(context,AppWidgetManager.getInstance(context), appWidgetIds);
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
                        TRANSLOC_API_KEY,
                        atw.getAgencyID(),
                        ARRIVAL);
        client.arrivalEstimates(atw.getAgencyID(), atw.getRouteID(), atw.getStopID())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((arrivals) -> {
                            handleWidgetUpdate(arrivals, appWidgetManager, atw, context, appWidgetId);
                        },
                        e -> {
                            handleServiceErrors(e, context);
                        }
                );
    }

    private void handleServiceErrors(Throwable e, Context context) {
        Log.e(TAG, "Error in getting list of arrival times", e);
        Toast.makeText(context, "Error - No data connection.", Toast.LENGTH_LONG).show();
    }

    private void handleWidgetUpdate(List<TransLocArrival> arrivals, AppWidgetManager appWidgetManager, ArrivalTimeWidget atw, Context context, int appWidgetId) {
        if(arrivals != null) {
            if(arrivals.isEmpty()) {
                Log.d(TAG, "arrivals is empty");
                Toast.makeText(context, "No arrival times found. Please try again later", Toast.LENGTH_LONG).show();
                atw.setMinutesUntilArrival(-1);
            } else {
                TransLocArrival nextArrival = arrivals.get(0);
                int minsTillArrival = getMinsUntilArrival(nextArrival);
                atw.setMinutesUntilArrival(minsTillArrival);
            }
            RemoteViews remoteViews = Utils.createRemoteViews(context, atw, appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        } else {
            Log.e(TAG, "arrivals is null!");
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
