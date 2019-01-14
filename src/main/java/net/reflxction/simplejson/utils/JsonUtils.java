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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Contains various helping methods for JSON
 */
public class JsonUtils {

    // Cannot be initiated
    private JsonUtils() {
    }

    /**
     * Converts a JSON string to be pretty (by fixing whitespaces, etc.)
     *
     * @param json JSON to parse
     * @return The given JSON string in a pretty manner
     */
    public static String setPretty(String json) {
        return Gsons.PRETTY_PRINTING.toJson(getObjectFromString(json));
    }

    /**
     * Returns a Google {@link JsonObject} from the given JSON text
     *
     * @param json Json text to parse
     * @return JsonObject from the given JSON string
     */
    public static JsonObject getObjectFromString(String json) {
        return Gsons.DEFAULT.fromJson(json, JsonElement.class).getAsJsonObject();
    }

    /**
     * Converts the given JSON text to a {@link Map}, which has all the strings (keys) assigned to their
     * values according to the given JSON.
     *
     * @param json JSON to convert
     * @return A Map with the JSON content
     */
    public static Map<String, Object> toMap(String json) {
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        return Gsons.DEFAULT.fromJson(json, type);
    }

    /**
     * Converts the given JSON text to a {@link List} which has all the elements in the JSON text in the
     * correct order
     *
     * @param json JSON to convert
     * @return A List with the JSON content
     */
    public static List<Object> toList(String json) {
        Type listType = new TypeToken<List<String>>() {
        }.getType();
        return Gsons.DEFAULT.fromJson(json, listType);
    }

}
