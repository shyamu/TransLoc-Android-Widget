package com.shyamu.translocwidget.bl;

import android.content.Context;
import android.util.Log;

import com.shyamu.translocwidget.ArrivalTimeWidget;
import com.shyamu.translocwidget.R;
import com.shyamu.translocwidget.Utils;
import com.shyamu.translocwidget.rest.model.TransLocArrival;
import com.shyamu.translocwidget.rest.service.ServiceGenerator;
import com.shyamu.translocwidget.rest.service.TransLocClient;

import org.joda.time.DateTime;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;

import static com.shyamu.translocwidget.Utils.TransLocDataType.ARRIVAL;

/**
 * Created by Shyamal on 3/16/2015.
 */
public class ArrivalTimeCalculator {
    private static final String TAG = "ArrivalTimeCalculator";
    ArrivalTimeWidget atw;
    Context context;

    public ArrivalTimeCalculator(Context context, ArrivalTimeWidget atw) {
        this.context = context;
        this.atw = atw;
    }

    public void getArrivalsFromService() {
        TransLocClient client =
                ServiceGenerator.createService(TransLocClient.class,
                        Utils.BASE_URL,
                        context.getString(R.string.mashape_key),
                        atw.getAgencyID(),
                        ARRIVAL);
        client.arrivalEstimates(atw.getAgencyID(), atw.getRouteID(), atw.getStopID())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleArrivalTime,
                        e -> Log.e(TAG, "Error in getting list of arrival times", e)
                );
    }

    private void handleArrivalTime(List<TransLocArrival> arrivals) {
        if(arrivals != null && !arrivals.isEmpty()) {
            TransLocArrival nextArrival = arrivals.get(0);

            int minsTillArrival = getMinsUntilArrival(nextArrival);
            atw.setMinutesUntilArrival(minsTillArrival);

        } else {
            Log.e(TAG, "arrivals is null or empty!");
        }
    }

    private int getMinsUntilArrival(TransLocArrival arrival) {
        DateTime currentDate = new DateTime();
        DateTime arrivalDate = new DateTime(arrival.arrivalAt);
        atw.setNextArrivalTime(arrivalDate);
        return Utils.getMinutesBetweenTimes(currentDate, arrivalDate);
    }

    public ArrivalTimeWidget getArrivalTimeWidget() {
        return atw;
    }

    public ArrivalTimeWidget getArrivalTimeWidgetWithUpdatedTime() {
        getArrivalsFromService();
        return atw;
    }


}
