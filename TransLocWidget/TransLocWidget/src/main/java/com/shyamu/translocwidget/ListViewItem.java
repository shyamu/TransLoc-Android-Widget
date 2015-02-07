package com.shyamu.translocwidget;

/**
 * Created by Shyamal on 2/6/2015.
 */
public class ListViewItem {
    private String agencyName;
    private String routeName;
    private String stopName;

    public ListViewItem(String agencyName, String routeName, String stopName) {
        this.agencyName = agencyName;
        this.routeName = routeName;
        this.stopName = stopName;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }
}
