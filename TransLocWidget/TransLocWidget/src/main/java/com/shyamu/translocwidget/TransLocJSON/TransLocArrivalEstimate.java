package com.shyamu.translocwidget.TransLocJSON;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TransLocArrivalEstimate{

    @SerializedName("arrivals")
    public List<TransLocArrival> arrivals;

    @SerializedName("agency_id")
    public int agencyId;

    @SerializedName("stop_id")
    public int stopId;
}