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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A utility to create and build JSON text flexibly
 */
public class JsonBuilder {

    // The map to assign values in
    private Map<String, Object> jsonMap = new HashMap<>();

    /**
     * Maps the given value to the specified key
     *
     * @param key   Key to assign to
     * @param value Value assigned to the key
     * @return This object instance
     */
    public JsonBuilder map(String key, Object value) {
        jsonMap.put(key, value);
        return this;
    }

    /**
     * Removes the given key (hence its value as well) from the JSON map and the mapping
     *
     * @param key Key to remove
     * @return This object instance
     */
    public JsonBuilder removeKey(String key) {
        jsonMap.remove(key);
        return this;
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
        return Gsons.DEFAULT.toJson(jsonMap);
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
        return Gsons.PRETTY_PRINTING.toJson(jsonMap);
    }

    /**
     * Constructs a {@link JSONObject} from the built/mapped JSON.
     *
     * @return The constructed JSON object
     */
    public JSONObject buildJSONObject() {
        return new JSONObject(build());
    }

}
