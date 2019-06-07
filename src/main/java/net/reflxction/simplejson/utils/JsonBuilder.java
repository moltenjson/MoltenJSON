/*
 * * Copyright 2018 github.com/ReflxctionDev
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
package net.reflxction.simplejson.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * A utility to create and build JSON text flexibly
 */
public class JsonBuilder {

    /**
     * The container map, which contains all the keys associated to their object
     */
    private final Map<String, Object> jsonMap;

    /**
     * Initiates a new JSON builder
     *
     * @param order Whether it should keep the order of the elements or not.
     */
    public JsonBuilder(boolean order) {
        jsonMap = order ? new LinkedHashMap<>() : new HashMap<>();
    }

    /**
     * Initiates a new JSON builder, and keeps the order of elements.
     */
    public JsonBuilder() {
        this(true);
    }

    /**
     * Initiates a new JSON builder, which contains the content of the given JSON object
     *
     * @param object Object to construct from
     */
    public JsonBuilder(JsonObject object) {
        Checks.notNull(object);
        jsonMap = JsonUtils.toMap(object);
    }

    /**
     * Initiates a new JSON builder, using the given Map
     *
     * @param jsonMap Map to use
     */
    public JsonBuilder(Map<String, Object> jsonMap) {
        Objects.requireNonNull(jsonMap, "Map<String, Object> (jsonMap) cannot be null");
        this.jsonMap = jsonMap;
    }

    /**
     * Maps the given value to the specified key
     *
     * @param key   Key to assign to
     * @param value Value assigned to the key
     * @return This builder instance
     */
    public JsonBuilder map(String key, Object value) {
        jsonMap.put(key, value);
        return this;
    }

    /**
     * Maps the given value to the specific key if the given {@link Predicate} is met, otherwise this
     * method will have no effect.
     *
     * @param predicate Criteria to test for the value. If null, the value will get mapped.
     * @param key       Key to assign to
     * @param value     Value assigned to the key
     * @param <T>       Object instance assignment
     * @return This builder instance
     */
    public <T> JsonBuilder mapIf(Predicate<T> predicate, String key, T value) {
        if (predicate == null || predicate.test(value))
            return map(key, value);
        return this;
    }

    /**
     * Maps the given value to the specific key if the value is not {@code null}. Otherwise, the method has
     * no effect.
     *
     * @param key   Key to assign to
     * @param value Value assigned to the key, must not be null.
     * @return This builder instance
     */
    public JsonBuilder mapIfNotNull(String key, Object value) {
        return mapIf(Objects::nonNull, key, value);
    }

    /**
     * Maps the given value to the specific key as long as the JSON map does not contain it (absent)
     *
     * @param key   Key to assign to
     * @param value Value assigned to the key
     * @return This builder instance
     */
    public JsonBuilder mapIfAbsent(String key, Object value) {
        return mapIf(o -> jsonMap.containsKey(key), key, value);
    }

    /**
     * Removes the given key (hence its value as well) from the JSON map and the mapping
     *
     * @param key Key to remove
     * @return This builder instance
     */
    public JsonBuilder removeKey(String key) {
        jsonMap.remove(key);
        return this;
    }

    /**
     * Returns all the assigned objects to their keys as a {@link Map}
     *
     * @return The JSON map
     */
    public Map<String, Object> getJsonMap() {
        return jsonMap;
    }

    /**
     * Builds the JSON and returns it as a {@link String}
     * <p>
     * Note that this will not be prettied. If pretty printing is desired,
     * use {@link #buildPretty()}
     *
     * @return The mapped JSON string
     */
    public String build() {
        return build(Gsons.DEFAULT);
    }

    /**
     * Builds the JSON and returns it as a {@link String}
     * <p>
     * Note that this will be pe prettied (with indentation fixed, etc.) If this is not
     * desired, use {@link #build()}
     *
     * @return The mapped JSON string, prettified.
     */
    public String buildPretty() {
        return build(Gsons.PRETTY_PRINTING);
    }

    /**
     * Builds the JSON using the given {@link Gson} profile returns it as a {@link String}.
     *
     * @param profile GSON profile to construct from
     * @return The mapped JSON string
     */
    public String build(Gson profile) {
        Checks.notNull(profile);
        return profile.toJson(jsonMap);
    }

    /**
     * Constructs a {@link JsonObject} from the built/mapped JSON.
     *
     * @return The constructed JSON object
     */
    public JsonObject buildJsonObject() {
        return buildJsonElement().getAsJsonObject();
    }

    /**
     * Constructs a {@link JsonElement} from the built/mapped JSON.
     *
     * @return The constructed JSON object
     */
    public JsonElement buildJsonElement() {
        return JsonUtils.getElementFromString(build());
    }

}
