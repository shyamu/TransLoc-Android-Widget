package com.shyamu.translocwidget.rest.service;

import com.shyamu.translocwidget.rest.model.TransLocAgency;
import com.shyamu.translocwidget.rest.model.TransLocRoute;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Shyamal on 3/15/2015.
 */
public interface TransLocClient {
    @GET("/agencies.json")
    Observable<List<TransLocAgency>> agencies();

    @GET("/routes.json")
    Observable<List<TransLocRoute>> routes(@Query("agencies") String agencyId);
}
