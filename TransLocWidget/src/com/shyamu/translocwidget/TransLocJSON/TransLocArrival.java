package com.shyamu.translocwidget.TransLocJSON;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

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