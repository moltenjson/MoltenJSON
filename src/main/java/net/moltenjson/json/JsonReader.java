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
import net.moltenjson.exceptions.JsonParseException;
import net.moltenjson.utils.Gsons;
import net.moltenjson.utils.JsonUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Consumer;

/**
 * Reads and parses JSON data from JSON files
 */
public class JsonReader implements Closeable, Lockable<JsonReader>, Refreshable<JsonReader> {

    /**
     * The JSON file to read from
     */
    private JsonFile file;

    /**
     * A buffered reader to handle reading and managing IO for the reader, while using GSON to parse
     */
    private final BufferedReader bufferedReader;

    /**
     * Whether to allow calls for {@link #setFile(JsonFile)} or not
     */
    private final boolean locked;

    /**
     * Initiates a new JSON file reader
     *
     * @param file   File to read for
     * @param locked Whether to allow calls to {@link #setFile(JsonFile)} or not
     * @throws IOException I/O exceptions while connecting with the file
     */
    public JsonReader(@NotNull JsonFile file, boolean locked) throws IOException {
        this.file = file;
        bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file.getFile()), StandardCharsets.UTF_8));
        this.locked = locked;
    }

    /**
     * Initiates a new JSON file reader
     *
     * @param file File to read for
     * @throws IOException I/O exceptions while connecting with the file
     */
    public JsonReader(@NotNull JsonFile file) throws IOException {
        this(file, false);
    }

    /**
     * Initiates a new JSON writer from a {@link BufferedReader}.
     * <p>
     * This is recommended when you want to read a resource/file that is embedded
     * inside your project resources
     *
     * @param reader Reader to initiate from
     * @param locked Whether to allow calls to {@link #setFile(JsonFile)} or not
     */
    public JsonReader(@NotNull BufferedReader reader, boolean locked) {
        bufferedReader = reader;
        this.locked = locked;
    }

    /**
     * Initiates a new JSON writer from a {@link BufferedReader}.
     * <p>
     * This is recommended when you want to read a resource/file that is embedded
     * inside your project resources
     *
     * @param reader Reader to initiate from
     */
    public JsonReader(@NotNull BufferedReader reader) {
        this(reader, false);
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
     * Reads and parses data from JSON, and returns an instance of the given object assignment
     * <p>
     * After the reader finishes reading, you are better off call {@link JsonReader#close()} to close
     * the IO connection. This is to avoid IO issues and ensures safety for the file and the JVM,
     * beside better management for the finite file resources.
     *
     * @param type Type which contains the object
     * @param <T>  The given object assignment
     * @return The object assigned, after parsing from JSON
     */
    public <T> T deserializeAs(@NotNull Type type) {
        return deserializeAs(type, Gsons.DEFAULT);
    }

    /**
     * Reads and parses data from JSON, and returns an instance of the given object assignment, using the provided
     * GSON profile.
     * <p>
     * After the reader finishes reading, you are better off call {@link JsonReader#close()} to close
     * the IO connection. This is to avoid IO issues and ensures safety for the file and the JVM,
     * beside better management for the finite file resources.
     *
     * @param type Type which contains the object
     * @param gson Gson profile to use
     * @param <T>  The given object assignment
     * @return The object assigned, after parsing from JSON
     */
    public <T> T deserializeAs(@NotNull Type type, @NotNull Gson gson) {
        T result = gson.fromJson(bufferedReader, type);
        if (result == null)
            throw new JsonParseException("Could not parse JSON from file " + getFile().getPath() + ". Object to parse: " + type.getTypeName());
        return result;
    }

    /**
     * Reads and parses the given JSON part of the file, and returns it as a deserialized instance of the
     * object assignment.
     * <p>
     * After the reader finishes reading, you are better off call {@link JsonReader#close()} to close
     * the IO connection. This is to avoid IO issues and ensures safety for the file and the JVM,
     * beside better management for the finite file resources.
     *
     * @param element Element to opt from the file and deserialize
     * @param type    Type to deserialize as and create an instance from.
     * @param <T>     The given object assignment
     * @return The deserialized object instance
     */
    public <T> T deserialize(@NotNull String element, @NotNull Type type) {
        return deserialize(element, type, Gsons.DEFAULT);
    }

    /**
     * Reads and parses the given JSON part of the file, and returns it as a deserialized instance of the
     * object assignment using the provided GSON profile
     * <p>
     * After the reader finishes reading, you are better off call {@link JsonReader#close()} to close
     * the IO connection. This is to avoid IO issues and ensures safety for the file and the JVM,
     * beside better management for the finite file resources.
     *
     * @param element Element to opt from the file and deserialize
     * @param type    Type to deserialize as and create an instance from.
     * @param gson    Gson profile to use
     * @param <T>     The given object assignment
     * @return The deserialized object instance
     */
    public <T> T deserialize(@NotNull String element, @NotNull Type type, @NotNull Gson gson) {
        JsonObject object = getJsonObject();
        return gson.fromJson(object.get(element), type);
    }

    /**
     * Returns a {@link JsonObject} from the file, which can be used to parse content separately
     * rather than deserializing an entire object.
     * <p>
     * For deserializing objects, see {@link #deserializeAs(Type)}
     * <p>
     * Any exceptions inside this method are not handled (no stacktrace, debugging, etc.),
     * to handle exceptions inside this method, use {@link #getJsonObject(Consumer)}
     *
     * @return A JSONObject from the file
     */
    public JsonObject getJsonObject() {
        return getJsonObject(null);
    }

    /**
     * Returns a {@link JsonObject} from the file, which can be used to parse content separately
     * rather than deserializing an entire object.
     * <p>
     * For deserializing objects, see {@link #deserializeAs(Type)}
     * <p>
     * To leave exceptions unhandled (no stacktrace, etc.), use {@link #getJsonObject()}
     *
     * @param onError A consumer for handling errors inside the try/catch of the
     *                parsing methods.
     * @return A JSONObject from the file
     */
    public JsonObject getJsonObject(@Nullable Consumer<IOException> onError) {
        return getJsonElement(onError).getAsJsonObject();
    }

    /**
     * Returns a {@link JsonElement} from the file, which can be used to parse content separately
     * rather than deserializing an entire object.
     * <p>
     * For deserializing objects, see {@link #deserializeAs(Type)}
     * <p>
     * To leave exceptions unhandled (no stacktrace, etc.), use {@link #getJsonObject()}
     *
     * @param onError A consumer for handling errors inside the try/catch of the
     *                parsing methods.
     * @return A JSONObject from the file
     */
    public JsonElement getJsonElement(Consumer<IOException> onError) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(file.getPath()));
            String json = new String(encoded, StandardCharsets.UTF_8);
            return JsonUtils.getElementFromString(json);
        } catch (IOException e) {
            if (onError != null)
                onError.accept(e);
            return null;
        }
    }

    /**
     * Returns a {@link JsonElement} from the file, which can be used to parse content separately
     * rather than deserializing an entire object.
     * <p>
     * For deserializing objects, see {@link #deserializeAs(Type)}
     * <p>
     * Any exceptions inside this method are not handled (no stacktrace, debugging, etc.),
     * to handle exceptions inside this method, use {@link #getJsonElement(Consumer)}
     *
     * @return A JSONObject from the file
     */
    public JsonElement getJsonElement() {
        return getJsonElement(null);
    }

    /**
     * Sets the new file. Implementation of this method should also update any content
     * this component controls.
     *
     * @param file New JSON file to use. Must not be null
     * @return This object instance
     */
    public JsonReader setFile(@NotNull JsonFile file) {
        checkLocked("Cannot invoke #setFile() on a locked JsonReader!");
        this.file = file;
        return this;
    }

    /**
     * Returns whether to allow calls to {@link #setFile(JsonFile)} or not.
     *
     * @return Whether to allow calls to {@link #setFile(JsonFile)} or not.
     */
    public boolean isLocked() {
        return locked;
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
        bufferedReader.close();
    }

    /**
     * Returns a new {@link JsonReader} and throws unchecked exceptions if there were any IO exceptions
     *
     * @param file JSON file to read from
     * @return The JsonReader object
     */
    public static JsonReader of(@NotNull JsonFile file) {
        try {
            return new JsonReader(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a new {@link JsonReader} and throws unchecked exceptions if there were any IO exceptions
     *
     * @param file   JSON file to read from
     * @param locked Whether to allow calls to {@link #setFile(JsonFile)} or not
     * @return The JsonReader object
     */
    public static JsonReader of(@NotNull JsonFile file, boolean locked) {
        try {
            return new JsonReader(file, locked);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates the cached content with with whatever is found in the file.
     *
     * @return The appropriate object to return when the component is refreshed
     */
    @Override
    public JsonReader refresh() {
        return setFile(file);
    }
}
