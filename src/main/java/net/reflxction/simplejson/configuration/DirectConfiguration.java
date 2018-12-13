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
package net.reflxction.simplejson.configuration;

import com.google.gson.JsonObject;
import net.reflxction.simplejson.json.JsonFile;
import net.reflxction.simplejson.json.JsonWriter;
import net.reflxction.simplejson.utils.Gsons;
import net.reflxction.simplejson.utils.JsonUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Represents a configuration file which makes <s>direct</s> contact with the
 * configuration. Direct contact is achieved from <s>getter</s> and <s>setter</s> methods
 * and eventually saves with {@link DirectConfiguration#save(Consumer)}
 *
 * @see net.reflxction.simplejson.configuration.select.SelectableConfiguration
 */
public class DirectConfiguration {

    // JSON writer
    private JsonWriter writer;

    // The cached JSON content on config creation
    private JsonObject content;

    /**
     * Initiates a new configuration for the given addon name.
     *
     * @param file JSON file to contact
     */
    public DirectConfiguration(JsonFile file) throws IOException {
        JsonFile jsonFile = new JsonFile(file.getFile());
        writer = new JsonWriter(jsonFile);
        content = writer.getCachedContent();
    }

    /**
     * Returns a {@link String} from the associated key.
     *
     * @param key Key to fetch from
     * @return The associated string
     */
    public String getString(String key) {
        return content.get(key).getAsString();
    }

    /**
     * Returns a {@code int} from the associated key.
     *
     * @param key Key to fetch from
     * @return The associated integer
     */
    public int getInt(String key) {
        return content.get(key).getAsInt();
    }

    /**
     * Returns a {@code double} from the associated key.
     *
     * @param key Key to fetch from
     * @return The associated double
     */
    public double getDouble(String key) {
        return content.get(key).getAsDouble();
    }

    /**
     * Returns a {@code long} from the associated key.
     *
     * @param key Key to fetch from
     * @return The associated long
     */
    public long getLong(String key) {
        return content.get(key).getAsLong();
    }

    /**
     * Returns a {@code float} from the associated key.
     *
     * @param key Key to fetch from
     * @return The associated float
     */
    public float getFloat(String key) {
        return content.get(key).getAsFloat();
    }

    /**
     * Returns a {@link BigDecimal} from the associated key.
     *
     * @param key Key to fetch from
     * @return The associated decimal
     */
    public BigDecimal getBigDecimal(String key) {
        return content.get(key).getAsBigDecimal();
    }

    /**
     * Returns a {@link List} of objects from the associated key
     *
     * @param key Key to fetch from
     * @return The associated List
     */
    public List<Object> getList(String key) {
        return JsonUtils.toList(content.get(key).toString());
    }

    /**
     * Returns a {@link Map} with values linked appropriately to their keys in order.
     *
     * @param key Key to fetch from
     * @return The associated Map
     */
    public Map<String, Object> getMap(String key) {
        return JsonUtils.toMap(content.get(key).toString());
    }

    /**
     * Returns the content of the JSON writer
     * @return
     */
    public JsonObject getContent() {
        return content;
    }

    /**
     * Assigns the given element to the key, and writes it to the JSON file.
     *
     * @param key   Key to assign to
     * @param value Value to assign to the key
     */
    public void set(String key, Object value) {
        content.add(key, Gsons.DEFAULT.toJsonTree(value));
    }

    /**
     * Removes the given key from the JSON file.
     * <p>
     * This will have no effect if the given key doesn't exist.
     *
     * @param key Key to remove
     */
    public void remove(String key) {
        content.remove(key);
    }

    /**
     * Saves the configuration
     *
     * @param onException The task to execute on exception. If no exception handling is required,
     *                    this may be left null.
     */
    public void save(Consumer<IOException> onException) {
        try {
            writer.writeAndOverride(content, true);
        } catch (IOException e) {
            onException.accept(e);
        }
    }

    /**
     * Returns a new {@link DirectConfiguration} and throws unchecked exceptions if there were any IO exceptions
     *
     * @param file JSON file to contact
     * @return The DirectConfiguration object
     */
    public static DirectConfiguration of(JsonFile file) {
        try {
            return new DirectConfiguration(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
