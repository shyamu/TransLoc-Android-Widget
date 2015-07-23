package com.shyamu.translocwidget.listview;

/**
 * Created by Shyamal on 2/6/2015.
 */
class ListViewItem {
    private String agencyName;
    private String routeName;
    private String stopName;
    private int backgroundColor;
    private int textColor;


    public ListViewItem(String agencyName, String routeName, String stopName, int backgroundColor, int textColor) {
        this.agencyName = agencyName;
        this.routeName = routeName;
        this.stopName = stopName;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
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

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
}
