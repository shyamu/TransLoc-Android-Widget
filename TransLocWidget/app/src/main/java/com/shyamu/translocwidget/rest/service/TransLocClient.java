package com.shyamu.translocwidget.rest.service;

import com.shyamu.translocwidget.rest.model.TransLocArrival;
import com.shyamu.translocwidget.rest.model.TransLocStop;
import com.shyamu.translocwidget.rest.model.TransLocAgency;
import com.shyamu.translocwidget.rest.model.TransLocRoute;

import java.util.List;

import retrofit.http.GET;
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

    @GET("/stops.json")
    Observable<List<TransLocStop>> stops(@Query("agencies")String agencyId);

    @GET("/arrival-estimates.json")
    Observable<List<TransLocArrival>> arrivalEstimates(
            @Query("agencies") String agencyId,
            @Query("routes") String routeId,
            @Query("stops") String stopId
    );
}
