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
package net.moltenjson.configuration.direct;

import com.google.gson.Gson;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.moltenjson.configuration.select.SelectableConfiguration;
import net.moltenjson.json.JsonFile;
import net.moltenjson.json.JsonWriter;
import net.moltenjson.json.Lockable;
import net.moltenjson.json.Refreshable;
import net.moltenjson.utils.Gsons;
import net.moltenjson.utils.JsonUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
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
 * @see SelectableConfiguration
 */
public class DirectConfiguration implements Lockable<DirectConfiguration>, Refreshable<DirectConfiguration> {

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
    public DirectConfiguration(@NotNull JsonFile file, boolean locked) throws IOException {
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
    public DirectConfiguration(@NotNull JsonFile file) throws IOException {
        this(file, false);
    }

    /**
     * Returns a {@link String} from the associated key.
     *
     * @param key Key to fetch from
     * @return The associated string
     */
    public final String getString(@NotNull String key) {
        return content.get(key).getAsString();
    }

    /**
     * Returns a {@code int} from the associated key.
     *
     * @param key Key to fetch from
     * @return The associated integer
     */
    public final int getInt(@NotNull String key) {
        return content.get(key).getAsInt();
    }

    /**
     * Returns a {@code double} from the associated key.
     *
     * @param key Key to fetch from
     * @return The associated double
     */
    public final double getDouble(@NotNull String key) {
        return content.get(key).getAsDouble();
    }

    /**
     * Returns a {@code long} from the associated key.
     *
     * @param key Key to fetch from
     * @return The associated long
     */
    public final long getLong(@NotNull String key) {
        return content.get(key).getAsLong();
    }

    /**
     * Returns a {@code float} from the associated key.
     *
     * @param key Key to fetch from
     * @return The associated float
     */
    public final float getFloat(@NotNull String key) {
        return content.get(key).getAsFloat();
    }

    /**
     * Returns a {@code boolean} from the associated boolean
     *
     * @param key Key to fetch from
     * @return The associated boolean
     */
    public final boolean getBoolean(@NotNull String key) {
        return content.get(key).getAsBoolean();
    }

    /**
     * Returns a {@link BigDecimal} from the associated key.
     *
     * @param key Key to fetch from
     * @return The associated decimal
     */
    public final BigDecimal getBigDecimal(@NotNull String key) {
        return content.get(key).getAsBigDecimal();
    }

    /**
     * Returns a {@link List} of objects from the associated key
     *
     * @param key Key to fetch from
     * @return The associated List
     */
    public final List<Object> getList(@NotNull String key) {
        return JsonUtils.toList(content.get(key).toString());
    }

    /**
     * Returns a {@link Map} with values linked appropriately to their keys in order.
     *
     * @param key Key to fetch from
     * @return The associated Map
     */
    public final Map<String, Object> getMap(@NotNull String key) {
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
    public final <T> T get(@NotNull String key, @NotNull Type type) {
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
    public final <T> T get(@NotNull String key, @NotNull Type type, @NotNull Gson gson) {
        return gson.fromJson(content.get(key), type);
    }

    /**
     * Returns a deserialized instance of the given class assignment, derived from the total
     * file content
     *
     * @param type Type to return an instance of, as a deserialized object
     * @param gson Gson profile to use
     * @param <T>  Class object assignment
     * @return The deserialized object
     */
    public final <T> T getAs(@NotNull Type type, @NotNull Gson gson) {
        return gson.fromJson(content, type);
    }

    /**
     * Returns a deserialized instance of the given class assignment, derived from the total
     * file content
     *
     * @param type Type to return an instance of, as a deserialized object
     * @param <T>  Class object assignment
     * @return The deserialized object
     */
    public final <T> T getAs(@NotNull Type type) {
        return getAs(type, Gsons.DEFAULT);
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
    public final void set(@NotNull String key, @Nullable Object value) {
        set(key, value == null ? JsonNull.INSTANCE : value, Gsons.PRETTY_PRINTING);
    }

    /**
     * Assigns the given element to the key, and writes it to the JSON file.
     *
     * @param key   Key to assign to
     * @param value Value to assign to the key
     * @param gson  Gson profile to use
     */
    public final void set(@NotNull String key, @NotNull Object value, @NotNull Gson gson) {
        content.add(key, gson.toJsonTree(value));
    }

    /**
     * Removes the given key from the JSON file.
     * <p>
     * This will have no effect if the given key does not exist.
     *
     * @param key Key to remove
     */
    public final void remove(@NotNull String key) {
        content.remove(key);
    }

    /**
     * Returns whether the specified key exists in the configuration data or not.
     *
     * @param key Key to check for
     * @return {@code true} if the member exists, {@code false} if otherwise.
     */
    public boolean contains(String key) {
        return content.has(key);
    }

    /**
     * Saves the configuration
     *
     * @param onException The task to execute on exception. If no exception handling is required,
     *                    this may be left null.
     */
    public final void save(@Nullable Consumer<IOException> onException) {
        save(Gsons.PRETTY_PRINTING, onException);
    }

    /**
     * Saves the configuration
     *
     * @param gson        The GSON profile to save with
     * @param onException The task to execute on exception. If no exception handling is required,
     *                    this may be left null.
     */
    public final void save(@NotNull Gson gson, @Nullable Consumer<IOException> onException) {
        try {
            writer.writeAndOverride(content, gson);
        } catch (IOException e) {
            if (onException != null)
                onException.accept(e);
        }
    }

    /**
     * Sets the new file. Implementation of this method should also update any content
     * this component controls.
     *
     * @param file New JSON file to use. Must not be null
     * @return This object instance
     */
    @Override
    public final DirectConfiguration setFile(@NotNull JsonFile file) {
        checkLocked("Cannot invoke #setFile() on a locked DirectConfiguration!");
        writer.setFile(file);
        content = writer.getCachedContentAsObject();
        return this;
    }

    /**
     * Updates the cached content with with whatever is found in the file.
     *
     * @return The appropriate object to return when the component is refreshed
     */
    @Override
    public final DirectConfiguration refresh() {
        return setFile(writer.getFile()); // This will update the content
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
     * Returns the {@link JsonFile} that this component controls
     *
     * @return The JsonFile that this component holds
     */
    @Override
    public JsonFile getFile() {
        return writer.getFile();
    }

    /**
     * Returns a new {@link DirectConfiguration} and throws unchecked exceptions if there were any IO exceptions
     *
     * @param file JSON file to contact
     * @return The DirectConfiguration object
     */
    public static DirectConfiguration of(@NotNull JsonFile file) {
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
    public static DirectConfiguration of(@NotNull JsonFile file, boolean locked) {
        try {
            return new DirectConfiguration(file, locked);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
