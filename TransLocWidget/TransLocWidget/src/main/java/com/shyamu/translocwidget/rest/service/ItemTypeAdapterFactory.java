package com.shyamu.translocwidget.rest.service;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Created by Shyamal on 3/15/2015.
 */
public class ItemTypeAdapterFactory implements TypeAdapterFactory {

    private String agencyId;

    public ItemTypeAdapterFactory(String agencyId) {
        this.agencyId = agencyId;
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
                    if (jsonObject.has("data") && jsonObject.get("data").isJsonArray()) {
                        Log.v("ItemTypeAdapterFactory", "Is agency or stop data");
                        jsonElement = jsonObject.get("data");
                    } else if (jsonObject.has("data") && jsonObject.get("data").isJsonObject() && agencyId != null) {
                        Log.v("ItemTypeAdapterFactory", "Is route data");
                        jsonElement = jsonObject.getAsJsonObject("data").getAsJsonArray(agencyId);
                    }
                }
                return delegate.fromJsonTree(jsonElement);
            }
        }.nullSafe();
    }
}
