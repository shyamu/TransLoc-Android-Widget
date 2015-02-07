package com.shyamu.translocwidget.TransLocJSON;

import com.fasterxml.jackson.annotation.JsonProperty;


public class TransLocRoute{

    @JsonProperty("route_id")
    public int routeID;
    @JsonProperty("short_name")
    public String shortName;
    @JsonProperty("long_name")
    public String longName;
    @JsonProperty("color")
    public String color;

    public String toString(){
        return shortName+" "+longName;
    }
}