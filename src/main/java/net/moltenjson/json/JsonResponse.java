/*
 * * Copyright 2019 github.com/moltenjson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.moltenjson.json;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.moltenjson.utils.Gsons;
import net.moltenjson.utils.JsonUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a JSON-formatted API response. This utility allows parsing JSON responses easily.
 * <p>
 * This utility does not necessarily have to be used for API responses specifically and exclusively. It can
 * work as a quick and short alternative to {@link JsonObject}, to allow accessing strings, numbers, lists, maps, and other
 * primitive types easily.
 *
 * @see JsonURLReader
 * @since SimpleJSON 2.0.2
 */
public class JsonResponse {

    /**
     * Represents the response JSON text
     */
    private final String responseText;

    /**
     * Represents the response as a {@link JsonObject}
     */
    private final JsonObject response;

    /**
     * Represents the GSON profile used to parse the JSON content.
     */
    private final Gson gson;

    /**
     * Initiates a new {@link JsonResponse} from the given JSON response, and parses it using the
     * provided GSON profile.
     *
     * @param response The response text. Must be a valid JSON.
     * @param gson     GSON profile to use
     */
    public JsonResponse(@NotNull String response, @NotNull Gson gson) {
        this.responseText = response;
        this.response = JsonUtils.getObjectFromString(response, this.gson = gson);
    }

    /**
     * Initiates a new {@link JsonResponse} from the given JSON response, and parses it using the default
     * GSON profile.
     *
     * @param response The response text. Must be a valid JSON.
     */
    public JsonResponse(@NotNull String response) {
        this(response, Gsons.DEFAULT);
    }

    /**
     * Returns a deserialized instance of the given class assignment.
     *
     * @param key  Key to fetch from
     * @param type Type to return an instance of, as a deserialized object
     * @param <T>  Class object assignment
     * @return The deserialized object
     */
    public <T> T get(@NotNull String key, @NotNull Type type) {
        return gson.fromJson(response.get(key), type);
    }


    /**
     * Returns a {@link String} from the associated key.
     *
     * @param key Key to fetch from
     * @return The associated string
     */
    public final String getString(@NotNull String key) {
        return response.get(key).getAsString();
    }

    /**
     * Returns a {@code int} from the associated key.
     *
     * @param key Key to fetch from
     * @return The associated integer
     */
    public final int getInt(@NotNull String key) {
        return response.get(key).getAsInt();
    }

    /**
     * Returns a {@code double} from the associated key.
     *
     * @param key Key to fetch from
     * @return The associated double
     */
    public final double getDouble(@NotNull String key) {
        return response.get(key).getAsDouble();
    }

    /**
     * Returns a {@code long} from the associated key.
     *
     * @param key Key to fetch from
     * @return The associated long
     */
    public final long getLong(@NotNull String key) {
        return response.get(key).getAsLong();
    }

    /**
     * Returns a {@code float} from the associated key.
     *
     * @param key Key to fetch from
     * @return The associated float
     */
    public final float getFloat(@NotNull String key) {
        return response.get(key).getAsFloat();
    }

    /**
     * Returns a {@code boolean} from the associated boolean
     *
     * @param key Key to fetch from
     * @return The associated boolean
     */
    public final boolean getBoolean(@NotNull String key) {
        return response.get(key).getAsBoolean();
    }

    /**
     * Returns a {@link BigDecimal} from the associated key.
     *
     * @param key Key to fetch from
     * @return The associated decimal
     */
    public final BigDecimal getBigDecimal(@NotNull String key) {
        return response.get(key).getAsBigDecimal();
    }

    /**
     * Returns a {@link List} of the specified object from the associated key
     *
     * @param key Key to fetch from
     * @param <E> List generic
     * @return The associated List
     */
    public final <E> List<E> getList(@NotNull String key) {
        Type type = new TypeToken<List<E>>() {
        }.getType();
        return get(key, type);
    }

    /**
     * Returns a {@link Map} with values linked appropriately to their keys in order.
     *
     * @param key Key to fetch from
     * @param <K> The map key generic
     * @param <V> The map value generic
     * @return The associated Map
     */
    public final <K, V> Map<K, V> getMap(@NotNull String key) {
        Type type = new TypeToken<LinkedHashMap<K, V>>() {
        }.getType();
        return get(key, type);
    }

    /**
     * Returns the response text, as provided in the constructor
     *
     * @return The response text
     */
    public String getResponseText() {
        return responseText;
    }

    /**
     * Returns the built {@link JsonObject} response
     *
     * @return The response as a {@link JsonObject}
     */
    public JsonObject getResponse() {
        return response;
    }

}
