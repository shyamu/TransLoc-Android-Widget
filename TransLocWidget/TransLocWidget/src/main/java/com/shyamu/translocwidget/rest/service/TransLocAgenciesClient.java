package com.shyamu.translocwidget.rest.service;

import com.shyamu.translocwidget.rest.model.TransLocAgency;

import java.util.List;

import retrofit.http.GET;
import rx.Observable;

/**
 * Created by Shyamal on 3/15/2015.
 */
public interface TransLocAgenciesClient {
    @GET("/agencies.json")
    Observable<List<TransLocAgency>> agencies();
}
