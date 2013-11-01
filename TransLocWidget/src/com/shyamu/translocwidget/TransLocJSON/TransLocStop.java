package com.shyamu.translocwidget.TransLocJSON;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransLocStop{

    @JsonProperty("code")
    public String code;

    @JsonProperty("description")
    public String description;

    @JsonProperty("url")
    public String url;

    @JsonProperty("parent_station_id")
    public String parentStationId;

    @JsonProperty("agency_ids")
    public List<Integer> agencyIds;

    @JsonProperty("station_id")
    public String stationId;

    @JsonProperty("location_type")
    public String locationType;

    @JsonProperty("location")
    public TransLocLocation location;

    @JsonProperty("stop_id")
    public int stopId;

    @JsonProperty("routes")
    public List<Integer> routes;

    @JsonProperty("name")
    public String name;

    public String toString(){
        return name;
    }
}