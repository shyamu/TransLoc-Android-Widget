package com.shyamu.translocwidget.TransLocJSON;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TransLocAgency{

    @JsonProperty("name")
    public String name;

    @JsonProperty("short_name")
    public String shortName;

    @JsonProperty("phone")
    public String phone;

    @JsonProperty("language")
    public String language;

    @JsonProperty("agency_id")
    public int agencyId;

    @JsonProperty("long_name")
    public String longName;

    @JsonProperty("bounding_box")
    public List<TransLocLocation> boundingBox;

    @JsonProperty("url")
    public String url;

    @JsonProperty("timezone")
    public String timezone;

    @JsonProperty("position")
    public TransLocLocation position;

    public String toString(){
        return longName;
    }
}