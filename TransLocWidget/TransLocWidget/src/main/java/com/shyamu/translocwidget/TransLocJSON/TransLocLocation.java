package com.shyamu.translocwidget.TransLocJSON;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransLocLocation{

    @JsonProperty("lat")
    public Double lat;

    @JsonProperty("lng")
    public Double lng;
}