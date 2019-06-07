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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.reflxction.simplejson.json.JsonFile;
import net.reflxction.simplejson.json.JsonWriter;
import net.reflxction.simplejson.json.Lockable;
import net.reflxction.simplejson.utils.Checks;
import net.reflxction.simplejson.utils.Gsons;
import net.reflxction.simplejson.utils.JsonUtils;
import net.reflxction.simplejson.utils.ObjectUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Represents a configuration file which makes <i>direct</i> contact with the
 * configuration. Direct contact is achieved from <i>getter</i> and <i>setter</i> methods
 * and eventually saves with {@link DirectConfiguration#save(Consumer)}
 * <p>
 * All methods which update or get a value are done on a local {@link JsonObject}, which is derived
 * on creation of this configuration. When {@link #save(Consumer)} is invoked, the content is saved
 * on the JSON file.
 *
 * @see net.reflxction.simplejson.configuration.select.SelectableConfiguration
 */
public class DirectConfiguration implements Lockable {

    /**
     * The JSON writer used to cache content and write to the file
     */
    private final JsonWriter writer;

    /**
     * The cached JSON content as a JsonObject
     */
    private JsonObject content;

    /**
     * Whether to allow calls to {@link #setFile(JsonFile)} or not
     */
    private final boolean locked;

    /**
     * Initiates a new configuration for given JSON file.
     *
     * @param file   JSON file to contact
     * @param locked Whether to allow calls to {@link #setFile(JsonFile)} or not
     * @throws IOException I/O exception while connecting with the file
     */
    public DirectConfiguration(JsonFile file, boolean locked) throws IOException {
        Objects.requireNonNull(file, "JsonFile (file) cannot be null");
        writer = new JsonWriter(file);
        this.locked = locked;
        content = writer.getCachedContentAsObject();
    }

    /**
     * Initiates a new configuration for the given addon name.
     *
     * @param file JSON file to contact
     * @throws IOException I/O exception while connecting with the file
     */
    public DirectConfiguration(JsonFile file) throws IOException {
        this(file, false);
    }

    /**
     * Returns a {@link String} from the associated key.
     *
     * @param key Key to fetch from
     * @return The associated string
     */
    public final String getString(String key) {
        Checks.notNull(key);
        return content.get(key).getAsString();
    }

    /**
     * Returns a {@code int} from the associated key.
     *
     * @param key Key to fetch from
     * @return The associated integer
     */
    public final int getInt(String key) {
        Checks.notNull(key);
        return content.get(key).getAsInt();
    }

    /**
     * Returns a {@code double} from the associated key.
     *
     * @param key Key to fetch from
     * @return The associated double
     */
    public final double getDouble(String key) {
        Checks.notNull(key);
        return content.get(key).getAsDouble();
    }

    /**
     * Returns a {@code long} from the associated key.
     *
     * @param key Key to fetch from
     * @return The associated long
     */
    public final long getLong(String key) {
        Checks.notNull(key);
        return content.get(key).getAsLong();
    }

    /**
     * Returns a {@code float} from the associated key.
     *
     * @param key Key to fetch from
     * @return The associated float
     */
    public final float getFloat(String key) {
        Checks.notNull(key);
        return content.get(key).getAsFloat();
    }

    /**
     * Returns a {@code boolean} from the associated boolean
     *
     * @param key Key to fetch from
     * @return The associated boolean
     */
    public final boolean getBoolean(String key) {
        Checks.notNull(key);
        return content.get(key).getAsBoolean();
    }

    /**
     * Returns a {@link BigDecimal} from the associated key.
     *
     * @param key Key to fetch from
     * @return The associated decimal
     */
    public final BigDecimal getBigDecimal(String key) {
        Checks.notNull(key);
        return content.get(key).getAsBigDecimal();
    }

    /**
     * Returns a {@link List} of objects from the associated key
     *
     * @param key Key to fetch from
     * @return The associated List
     */
    public final List<Object> getList(String key) {
        Checks.notNull(key);
        return JsonUtils.toList(content.get(key).toString());
    }

    /**
     * Returns a {@link Map} with values linked appropriately to their keys in order.
     *
     * @param key Key to fetch from
     * @return The associated Map
     */
    public final Map<String, Object> getMap(String key) {
        Checks.notNull(key);
        return JsonUtils.toMap(content.get(key).toString());
    }

    /**
     * Returns a deserialized instance of the given class assignment.
     *
     * @param key  Key to fetch from
     * @param type Type to return an instance of, as a deserialized object
     * @param <T>  Class object assignment
     * @return The deserialized object
     */
    public final <T> T get(String key, Type type) {
        Checks.notNull(key);
        return get(key, type, Gsons.PRETTY_PRINTING);
    }

    /**
     * Returns a deserialized instance of the given class assignment, using the given GSON profile
     *
     * @param key  Key to fetch from
     * @param type Type to return an instance of, as a deserialized object
     * @param gson Gson profile to use
     * @param <T>  Class object assignment
     * @return The deserialized object
     */
    public final <T> T get(String key, Type type, Gson gson) {
        Checks.notNull(key);
        return gson.fromJson(content.get(key), type);
    }

    /**
     * Returns the content of the JSON writer
     *
     * @return The JSON content
     */
    public final JsonObject getContent() {
        return content;
    }

    /**
     * Assigns the given element to the key, and writes it to the JSON file.
     *
     * @param key   Key to assign to
     * @param value Value to assign to the key
     */
    public final void set(String key, Object value) {
        set(key, value, Gsons.PRETTY_PRINTING);
    }

    /**
     * Assigns the given element to the key, and writes it to the JSON file.
     *
     * @param key   Key to assign to
     * @param value Value to assign to the key
     * @param gson  Gson profile to use
     */
    public final void set(String key, Object value, Gson gson) {
        Checks.notNull(key);
        Checks.notNull(value);
        Checks.notNull(gson);
        content.add(key, gson.toJsonTree(value));
    }

    /**
     * Removes the given key from the JSON file.
     * <p>
     * This will have no effect if the given key doesn't exist.
     *
     * @param key Key to remove
     */
    public final void remove(String key) {
        Checks.notNull(key);
        content.remove(key);
    }

    /**
     * Saves the configuration
     *
     * @param onException The task to execute on exception. If no exception handling is required,
     *                    this may be left null.
     */
    public final void save(Consumer<IOException> onException) {
        try {
            writer.writeAndOverride(content, true);
        } catch (IOException e) {
            ObjectUtils.ifNotNull(onException, task -> task.accept(e));
        }
    }

    /**
     * Sets the new file. Implementation of this method should also update any content
     * this component controls.
     *
     * @param file New JSON file to use. Must not be null
     */
    @Override
    public final void setFile(JsonFile file) {
        checkLocked("Cannot invoke #setFile() on a locked DirectConfiguration!");
        Checks.notNull(file);
        writer.setFile(file);
        content = writer.getCachedContentAsObject();
    }

    /**
     * Returns whether the current component is locked or not. This will control whether
     * {@link #setFile(JsonFile)} can be used or not.
     *
     * @return Whether the current component is locked or not.
     */
    @Override
    public boolean isLocked() {
        return locked;
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

    /**
     * Returns a new {@link DirectConfiguration} and throws unchecked exceptions if there were any IO exceptions
     *
     * @param file   JSON file to contact
     * @param locked Whether to allow calls to {@link #setFile(JsonFile)} or not
     * @return The DirectConfiguration object
     */
    public static DirectConfiguration of(JsonFile file, boolean locked) {
        try {
            return new DirectConfiguration(file, locked);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
