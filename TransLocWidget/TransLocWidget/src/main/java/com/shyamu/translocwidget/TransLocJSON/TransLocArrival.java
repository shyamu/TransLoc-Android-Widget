package com.shyamu.translocwidget.TransLocJSON;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class TransLocArrival{
    @JsonProperty("route_id")
    public int routeId;

    @JsonProperty("vehicle_id")
    public int vehicleId;

    @JsonProperty("arrival_at")
    public Date arrivalAt;

    @JsonProperty("type")
    public String type;
}