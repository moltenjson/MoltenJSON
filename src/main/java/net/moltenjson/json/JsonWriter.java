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
package net.moltenjson.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.moltenjson.configuration.direct.DirectConfiguration;
import net.moltenjson.configuration.select.SelectableConfiguration;
import net.moltenjson.utils.Gsons;
import net.moltenjson.utils.JsonUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Writes data and content to the JSON file.
 * <p>
 * All writing operations make <i>immediate IO calls</i>. This is mainly intended for <i>internal</i>
 * usage and for <i>one-time operations for writing or caching</i>. If your program tends to make a lot
 * of reading/writing operations, it's <i>heavily advised</i> to use Configurations.
 *
 * @see DirectConfiguration
 * @see SelectableConfiguration
 */
public class JsonWriter implements Closeable, Lockable<JsonWriter>, Refreshable<JsonWriter> {

    /**
     * Represents an empty JSON object. Instances of the content will be derived from this object if
     * the file content was a {@link JsonElement}.
     */
    private static final JsonObject EMPTY = new JsonObject();

    /**
     * A buffered writer used to manage IO connections
     */
    private BufferedWriter bufferedWriter;

    /**
     * The JSON file to write for
     */
    private JsonFile file;

    /**
     * The cached content of the file as a {@link JsonElement}
     */
    private JsonElement contentElement;

    /**
     * The cached content of the file
     */
    private JsonObject content;

    /**
     * A reader which caches content of the file as soon as this writer is created.
     */
    private JsonReader reader;

    /**
     * Whether to allow calls to {@link #setFile(JsonFile)} or not
     */
    private final boolean locked;

    /**
     * Initiates a new JSON writer
     *
     * @param file   JSON file to write to
     * @param locked Whether to allow calls to {@link #setFile(JsonFile)} or not
     * @throws IOException If there were IO issues whilst initiating the file writer
     */
    public JsonWriter(@NotNull JsonFile file, boolean locked) throws IOException {
        this.file = file;
        this.locked = locked;
        reader = new JsonReader(file);
        try {
            contentElement = reader.getJsonElement(Throwable::printStackTrace);
            content = contentElement.getAsJsonObject();
        } catch (IllegalStateException e) {
            content = EMPTY;
        }
        reader.close();
    }

    /**
     * Initiates a new JSON writer
     *
     * @param file JSON file to write to
     * @throws IOException If there were IO issues whilst initiating the file writer
     */
    public JsonWriter(@NotNull JsonFile file) throws IOException {
        this(file, false);
    }

    /**
     * Initiates a new JSON writer from a {@link BufferedWriter}.
     * <p>
     * This is recommended when you want to read a resource/file that is embedded
     * inside your project resources
     *
     * @param writer Writer to initiate from
     * @param locked Whether to allow calls to {@link #setFile(JsonFile)} or not
     */
    public JsonWriter(@NotNull BufferedWriter writer, boolean locked) {
        bufferedWriter = writer;
        this.locked = locked;
    }

    /**
     * Initiates a new JSON writer from a {@link BufferedWriter}.
     * <p>
     * This is recommended when you want to read a resource/file that is embedded
     * inside your project resources
     *
     * @param writer Writer to initiate from
     */
    public JsonWriter(@NotNull BufferedWriter writer) {
        this(writer, false);
    }

    /**
     * Writes the given content to the JSON file
     * <p>
     * You must keep in mind that you shouldn't call any methods like getName() when you want to save an object, as
     * full serializing will be applied to the object.
     * <p>
     * This method will override the entire content of the file, so if you want to add a key rather than overriding
     * everything, using {@link #add(String, Object, boolean, boolean)} would be more appropriate.
     *
     * @param jsonResult     JSON object to be saved
     * @param prettyPrinting Whether the writer should write it in a pretty manner
     * @throws IOException if it encountered IO issues while writing
     */
    public void writeAndOverride(@NotNull Object jsonResult, boolean prettyPrinting) throws IOException {
        writeAndOverride(jsonResult, prettyPrinting ? Gsons.PRETTY_PRINTING : Gsons.DEFAULT);
    }

    /**
     * Writes the given content to the JSON file, using the provided GSON profile
     * <p>
     * You must keep in mind that you shouldn't call any methods like getName() when you want to save an object, as
     * full serializing will be applied to the object.
     * <p>
     * This method will override the entire content of the file, so if you want to add a key rather than overriding
     * everything, using {@link #add(String, Object, boolean, boolean)} would be more appropriate.
     *
     * @param jsonResult JSON object to be saved
     * @param gson       GSON profile to use
     * @throws IOException if it encountered IO issues while writing
     */
    public void writeAndOverride(@NotNull Object jsonResult, @NotNull Gson gson) throws IOException {
        write(gson.toJson(jsonResult));
    }

    /**
     * Assigns the given element to the key, and writes it to the JSON file.
     * <p>
     * This will only add the element. If you want to override the content of the file, use {@link #writeAndOverride(Object, boolean)}
     *
     * @param key            Key to assign to
     * @param value          Value to assign to the key
     * @param prettyPrinting Whether to write the file in a prettified format
     * @param override       Whether to override the key value if it exists already or not.
     * @return The modified JsonObject
     * @throws IOException If the {@link BufferedWriter} encounters any I/O issues
     */
    public JsonObject add(@NotNull String key, @NotNull Object value, boolean prettyPrinting, boolean override) throws IOException {
        if (memberExists(key) && !override) return content;
        content.add(key, Gsons.DEFAULT.toJsonTree(value));
        write(prettyPrinting ? JsonUtils.setPretty(content.toString()) : content.toString());
        return content;
    }

    /**
     * Assigns the given element to the key, and writes it to the JSON file.
     * <p>
     * This will only add the element. If you want to override the content of the file, use {@link #writeAndOverride(Object, boolean)}
     * <p>
     * This will <i>NOT</i> override the value if it already exists. If you want to
     * override and set it regardless, use {@link #add(String, Object, boolean, boolean)}
     *
     * @param key            Key to assign to
     * @param value          Value to assign to the key
     * @param prettyPrinting Whether to write the file in a prettified format
     * @return The modified JsonObject
     * @throws IOException If the {@link BufferedWriter} encounters any I/O issues
     */
    public JsonObject add(@NotNull String key, @NotNull Object value, boolean prettyPrinting) throws IOException {
        return add(key, value, prettyPrinting, false);
    }

    /**
     * Assigns the given element to the key, and writes it to the JSON file.
     * <p>
     * This will only add the element. If you want to override the content of the file, use {@link #writeAndOverride(Object, boolean)}
     * <p>
     * This will <i>NOT</i> override the value if it already exists. If you want to
     * override and set it regardless, use {@link #add(String, Object, boolean, boolean)}
     * <p>
     * This will <i>NOT</i> write the file in prettified format. If you want to
     * write pretty content, use {@link #add(String, Object, boolean)}
     *
     * @param key   Key to assign to
     * @param value Value to assign to the key
     * @return The modified JsonObject
     * @throws IOException If the {@link BufferedWriter} encounters any I/O issues
     */
    public JsonObject add(@NotNull String key, @NotNull Object value) throws IOException {
        return add(key, value, false);
    }

    /**
     * Removes the given key from the JSON file.
     * <p>
     * This will have no effect if the given key does not exist.
     *
     * @param key            Key to remove
     * @param prettyPrinting Whether to use pretty printing when writing
     * @return The modified JsonObject
     * @throws IOException If the {@link BufferedWriter} encounters any I/O issues
     */
    public JsonObject removeKey(@NotNull String key, boolean prettyPrinting) throws IOException {
        content.remove(key);
        write(prettyPrinting ? JsonUtils.setPretty(content.toString()) : content.toString());
        return content;
    }

    /**
     * Removes the given key from the JSON file.
     * <p>
     * This will have no effect if the given key does not exist.
     * <p>
     * This will use pretty printing when writing.
     *
     * @param key Key to remove
     * @return The modified JsonObject
     * @throws IOException If the {@link BufferedWriter} encounters any I/O issues
     */
    public JsonObject removeKey(@NotNull String key) throws IOException {
        return removeKey(key, true);
    }

    /**
     * Returns {@code true} if the given member exists, or {@code false} if otherwise.
     *
     * @param key Key to check for
     * @return true if the value exists, false if otherwise.
     */
    public boolean memberExists(@NotNull String key) {
        return content.has(key);
    }

    /**
     * Returns the cached content which is created whenever a {@link JsonWriter} is initiated.
     * <p>
     * Mutative methods like {@link #add(String, Object)}, {@link #removeKey(String, boolean)} update
     * the content of the cached content, and write to the file.
     *
     * @return The cached JSON object
     */
    public JsonObject getCachedContentAsObject() {
        return content;
    }

    /**
     * Returns the cached content as a {@link JsonElement}, which is created whenever a {@link JsonWriter} is initiated.
     * <p>
     * Mutative methods like {@link #add(String, Object)}, {@link #removeKey(String, boolean)} update
     * the content of the cached content, and write to the file.
     *
     * @return The cached JSON object
     */
    public JsonElement getCachedContentAsElement() {
        return contentElement;
    }

    /**
     * A quick method to write text in UTF-8 charset format
     *
     * @param text Text to write
     * @throws IOException If I/O exceptions were encountered
     */
    private void write(@NotNull String text) throws IOException {
        if (bufferedWriter != null) bufferedWriter.write(text);
        else Files.write(Paths.get(file.getPath()), text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Sets the new file. Implementation of this method should also update any content
     * this component controls.
     *
     * @param file New JSON file to use. Must not be null
     * @return This object instance
     */
    @Override
    public JsonWriter setFile(@NotNull JsonFile file) {
        checkLocked("Cannot invoke #setFile() on a locked JsonWriter!");
        reader.setFile(file);
        this.file = file;
        contentElement = reader.getJsonElement(Throwable::printStackTrace);
        if (contentElement.isJsonObject()) content = reader.getJsonObject();
        return this;
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
        return file;
    }

    /**
     * Updates the cached content with with whatever is found in the file.
     *
     * @return The appropriate object to return when the component is refreshed
     */
    @Override
    public JsonWriter refresh() {
        return setFile(file);
    }

    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     *
     * <p> As noted in {@link AutoCloseable#close()}, cases where the
     * close may fail require careful attention. It is strongly advised
     * to relinquish the underlying resources and to internally
     * <em>mark</em> the {@code Closeable} as closed, prior to throwing
     * the {@code IOException}.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        if (bufferedWriter != null)
            bufferedWriter.close();
    }

    /**
     * Returns a new {@link JsonWriter} and throws unchecked exceptions if there were any IO exceptions
     *
     * @param file JSON file to write to
     * @return The JsonWriter object
     */
    public static JsonWriter of(@NotNull JsonFile file) {
        try {
            return new JsonWriter(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a new {@link JsonWriter} and throws unchecked exceptions if there were any IO exceptions
     *
     * @param file   JSON file to write to
     * @param locked Whether to allow calls to {@link #setFile(JsonFile)} or not
     * @return The JsonWriter object
     */
    public static JsonWriter of(@NotNull JsonFile file, boolean locked) {
        try {
            return new JsonWriter(file, locked);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
