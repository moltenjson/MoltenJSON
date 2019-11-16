/*
 * * Copyright 2018-2019 github.com/moltenjson
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
package net.moltenjson.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Contains various helping methods for JSON
 */
public class JsonUtils {

    private static final JsonParser PARSER = new JsonParser();

    private JsonUtils() {
        throw new AssertionError(JsonUtils.class.getName() + " cannot be initiated!");
    }

    /**
     * Converts a JSON string to be pretty (by fixing whitespaces, etc.)
     *
     * @param json JSON to parse
     * @return The given JSON string in a pretty manner
     */
    public static String setPretty(@NotNull String json) {
        return Gsons.PRETTY_PRINTING.toJson(getElementFromString(json));
    }

    /**
     * Returns a Google {@link JsonElement} from the given JSON text, using the given GSON profile.
     *
     * @param json Json text to parse
     * @param gson GSON profile to use
     * @return JsonElement from the given JSON string
     */
    public static JsonElement getElementFromString(@NotNull String json, @NotNull Gson gson) {
        return gson.fromJson(json, ReflectiveTypes.ELEMENT_TYPE);
    }

    /**
     * Returns a Google {@link JsonElement} from the given JSON text
     *
     * @param json Json text to parse
     * @return JsonElement from the given JSON string
     */
    public static JsonElement getElementFromString(@NotNull String json) {
        return PARSER.parse(json);
    }

    /**
     * Returns a Google {@link JsonObject} from the given JSON text, using the given GSON profile.
     *
     * @param json Json text to parse
     * @param gson GSON profile to use
     * @return JsonObject from the given JSON string
     */
    public static JsonObject getObjectFromString(@NotNull String json, @NotNull Gson gson) {
        return getElementFromString(json, gson).getAsJsonObject();
    }

    /**
     * Returns a Google {@link JsonObject} from the given JSON text
     *
     * @param json Json text to parse
     * @return JsonObject from the given JSON string
     */
    public static JsonObject getObjectFromString(@NotNull String json) {
        return getObjectFromString(json, Gsons.DEFAULT);
    }

    /**
     * Converts the given JSON text to a {@link Map}, which has all the strings (keys) assigned to their
     * values according to the given JSON.
     *
     * @param json JSON to convert
     * @return A Map with the JSON content
     */
    public static Map<String, Object> toMap(@NotNull String json) {
        return Gsons.DEFAULT.fromJson(json, ReflectiveTypes.MAP_TYPE);
    }

    /**
     * Converts the given JSON element to a {@link Map}, which has all the strings (keys) assigned to their
     * values according to the given JSON.
     *
     * @param json JSON to convert
     * @return A Map with the JSON content
     */
    public static Map<String, Object> toMap(@NotNull JsonElement json) {
        return Gsons.DEFAULT.fromJson(json, ReflectiveTypes.MAP_TYPE);
    }

    /**
     * Converts the given JSON text to a {@link List} which has all the elements in the JSON text in the
     * correct order
     *
     * @param json JSON to convert
     * @return A List with the JSON content
     */
    public static List<Object> toList(@NotNull String json) {
        return Gsons.DEFAULT.fromJson(json, ReflectiveTypes.LIST_TYPE);
    }

    /**
     * Converts the given JSON element to a {@link List}, which contains the objects inside that list
     *
     * @param json JSON to convert
     * @return A Map with the JSON content
     */
    public static List<Object> toList(@NotNull JsonElement json) {
        return Gsons.DEFAULT.fromJson(json, ReflectiveTypes.LIST_TYPE);
    }
}
