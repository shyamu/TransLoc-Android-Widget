package com.shyamu.translocwidget.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TransLocStop{

    @SerializedName("code")
    public String code;

    @SerializedName("agency_ids")
    public List<Integer> agencyIds;

    @SerializedName("location")
    public TransLocLocation location;

    @SerializedName("stop_id")
    public int stopId;

    @SerializedName("routes")
    public List<Integer> routes;

    @SerializedName("name")
    public String name;

    public String toString(){
        return name;
    }
}