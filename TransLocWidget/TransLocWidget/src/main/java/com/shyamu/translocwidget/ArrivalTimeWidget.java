package com.shyamu.translocwidget;

import android.content.Context;
import android.widget.RemoteViews;

import org.joda.time.DateTime;

/**
 * Created by Shyamal on 2/6/2015.
 */
public class ArrivalTimeWidget {
    private String agencyID;
    private String routeID;
    private String stopID;
    private String agencyLongName;
    private String routeName;
    private String stopName;
    private String arrivalTimesUrl;
    private DateTime nextArrivalTime;
    private int minutesUntilArrival;

    public ArrivalTimeWidget() {
    }

    public String getAgencyID() {
        return agencyID;
    }

    public void setAgencyID(String agencyID) {
        this.agencyID = agencyID;
    }

    public String getRouteID() {
        return routeID;
    }

    public void setRouteID(String routeID) {
        this.routeID = routeID;
    }

    public String getStopID() {
        return stopID;
    }

    public void setStopID(String stopID) {
        this.stopID = stopID;
    }

    public void setAgencyLongName(String agencyLongName) {
        this.agencyLongName = agencyLongName;
    }

    public String getAgencyLongName() {
        return agencyLongName;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public String getStopName() {
        return stopName;
    }

    public DateTime getNextArrivalTime() {
        return nextArrivalTime;
    }

    public void setNextArrivalTime(DateTime nextArrivalTime) {
        this.nextArrivalTime = nextArrivalTime;
    }

    public int getMinutesUntilArrival() {
        return minutesUntilArrival;
    }

    public void setMinutesUntilArrival(int minutesUntilArrival) {
        this.minutesUntilArrival = minutesUntilArrival;
    }

    public String toString() {
        return "ArrivalTimeWidget with info: " + agencyLongName + " | " + routeName + " | " + stopName;
    }

    private boolean isValidWidget() {
        return agencyID != null && routeID != null && stopID != null &&
               agencyLongName != null && routeName != null && stopName != null;
    }

    public String getArrivalTimesUrl() {
        if(isValidWidget()) {
            return Utils.GET_ARRIVAL_ESTIMATES_URL + getAgencyID() + "&routes=" + getRouteID() + "&stops=" + getStopID();
        } else {
            throw new IllegalStateException("ArrivalTimeWidget is not a valid widget");
        }
    }

    public RemoteViews createRemoteViews(Context context) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        //Set the time remaining of the widget
        remoteViews.setTextViewText(R.id.tvRemainingTime, Integer.toString(minutesUntilArrival));
        if (minutesUntilArrival < 1) remoteViews.setTextViewText(R.id.tvRemainingTime, "<1");
        if (minutesUntilArrival < 2) remoteViews.setTextViewText(R.id.tvMins, "min away");
        else remoteViews.setTextViewText(R.id.tvMins, "mins away");

        remoteViews.setTextViewText(R.id.tvRoute, routeName);
        remoteViews.setTextViewText(R.id.tvStop, stopName);

        return remoteViews;
    }

}
