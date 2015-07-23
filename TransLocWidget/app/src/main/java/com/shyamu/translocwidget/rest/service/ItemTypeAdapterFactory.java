package com.shyamu.translocwidget.rest.service;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import static com.shyamu.translocwidget.bl.Utils.*;

/**
 * Created by Shyamal on 3/15/2015.
 */
class ItemTypeAdapterFactory implements TypeAdapterFactory {

    private static final String TAG = "ItemTypeAdapterFactory";
    private String agencyId;
    private TransLocDataType dataType;

    public ItemTypeAdapterFactory(String agencyId, TransLocDataType dataType) {
        this.agencyId = agencyId;
        this.dataType = dataType;
    }

    public <T> TypeAdapter<T> create(Gson gson, final TypeToken<T> type) {

        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
        final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);

        return new TypeAdapter<T>() {

            public void write(JsonWriter out, T value) throws IOException {
                delegate.write(out, value);
            }

            public T read(JsonReader in) throws IOException {

                JsonElement jsonElement = elementAdapter.read(in);
                if (jsonElement.isJsonObject()) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    if (jsonObject.has("data") && ((jsonObject.get("data").isJsonObject()) || jsonObject.get("data").isJsonArray())) {
                        switch (dataType) {
                            case AGENCY:
                                Log.d(TAG, "dataType is AGENCY");
                                jsonElement = jsonObject.get("data");
                                break;
                            case ROUTE:
                                Log.d(TAG, "dataType is ROUTE");
                                jsonElement = jsonObject.getAsJsonObject("data").getAsJsonArray(agencyId);
                                break;
                            case STOP:
                                Log.d(TAG, "dataType is STOP");
                                jsonElement = jsonObject.get("data");
                                break;
                            case ARRIVAL:
                                Log.d(TAG, "dataType is ARRIVAL");
                                // if there are arrival times available send them, else send empty json array
                                if(jsonObject.getAsJsonArray("data").size() > 0){
                                    jsonElement = jsonObject.getAsJsonArray("data").get(0).getAsJsonObject().getAsJsonArray("arrivals");
                                } else {
                                    jsonElement = new JsonArray();
                                }
                                break;
                        }
                    }
                }
                return delegate.fromJsonTree(jsonElement);
            }
        }.nullSafe();
    }
}
