package com.shyamu.translocwidget.rest.model;

import com.google.gson.annotations.SerializedName;

public class TransLocAgency{

    @SerializedName("name")
    public String name;

    @SerializedName("short_name")
    public String shortName;

    @SerializedName("agency_id")
    public int agencyId;

    @SerializedName("long_name")
    public String longName;

    public String toString(){
        return longName;
    }
}