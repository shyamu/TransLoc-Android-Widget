package com.shyamu.translocwidget.rest.model;

import com.google.gson.annotations.SerializedName;


public class TransLocRoute{

    @SerializedName("route_id")
    public int routeID;

    @SerializedName("short_name")
    public String shortName;

    @SerializedName("long_name")
    public String longName;

    @SerializedName("color")
    public String color;

    public String toString(){
        return shortName + " " + longName;
    }
}