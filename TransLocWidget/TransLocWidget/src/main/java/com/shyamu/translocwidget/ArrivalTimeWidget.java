package com.shyamu.translocwidget;

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

    private boolean isValidWidget() {
        return agencyID != null && routeID != null && stopID != null &&
               agencyLongName != null && routeName != null && stopName != null;
    }

    public String getArrivalTimesUrl() {
        if(isValidWidget()) {
            return Utils.GET_ARRIVAL_ESTIMATES_URL + getAgencyID() + "&routes=" + getRouteID() + "&stops=" + getStopID();
        } else {
            return null;
        }
    }

}
