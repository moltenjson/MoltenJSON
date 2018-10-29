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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Contains various helping methods for JSON
 */
public class JsonUtils {

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
        JsonParser parser = new JsonParser();
        return parser.parse(json).getAsJsonObject();
    }

}
