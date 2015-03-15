package com.shyamu.translocwidget.services;

import com.shyamu.translocwidget.TransLocJSON.TransLocAgencies;

import java.util.List;

import retrofit.http.GET;

/**
 * Created by Shyamal on 3/15/2015.
 */
public interface TransLocAgenciesClient {
    @GET("/agencies.json/")
    List<TransLocAgencies> agencies();
}
