package com.shyamu.translocwidget.TransLocJSON;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TransLocArrivalEstimate{

    @JsonProperty("arrivals")
    public List<TransLocArrival> arrivals;

    @JsonProperty("agency_id")
    public int agencyId;

    @JsonProperty("stop_id")
    public int stopId;
}