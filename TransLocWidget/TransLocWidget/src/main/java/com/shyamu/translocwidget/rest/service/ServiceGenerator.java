package com.shyamu.translocwidget.rest.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

import static com.shyamu.translocwidget.bl.Utils.*;

/**
 * Created by Shyamal on 3/15/2015.
 */
public class ServiceGenerator {
    // No need to instantiate this class.
    private ServiceGenerator() {
    }

    public static <S> S createService(Class<S> serviceClass, String baseUrl, final String key, String agencyId, TransLocDataType type) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory(agencyId, type))
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();

        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(baseUrl)
                .setClient(new OkClient(new OkHttpClient()))
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(request -> request.addHeader("X-Mashape-Authorization", key));

        RestAdapter adapter = builder.build();
        return adapter.create(serviceClass);
    }
}
