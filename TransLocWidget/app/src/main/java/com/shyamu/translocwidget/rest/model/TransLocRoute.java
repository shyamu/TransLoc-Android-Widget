package com.shyamu.translocwidget.rest.model;

import com.google.gson.annotations.SerializedName;


public class TransLocRoute{

    @SerializedName("route_id")
    public int routeID;

    @SerializedName("short_name")
    private String shortName;

    @SerializedName("long_name")
    private String longName;

    @SerializedName("color")
    public String color;

    public String toString(){
        if(shortName == null || shortName.equals("")) {
            return longName;
        } else {
            return shortName + " - " + longName;
        }
    }
}