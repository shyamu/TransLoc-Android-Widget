package com.shyamu.translocwidget.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class TransLocArrival{
    @SerializedName("route_id")
    public int routeId;

    @SerializedName("vehicle_id")
    public int vehicleId;

    @SerializedName("arrival_at")
    public Date arrivalAt;

    @SerializedName("type")
    public String type;
}