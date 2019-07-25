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
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * A helping class for reading content of JSON files from URLs.
 *
 * @see JsonResponse
 */
public class JsonURLReader implements Refreshable<JsonElement> {

    /**
     * URL to read from
     */
    private final URL url;

    /**
     * The content of the URL
     */
    private JsonElement content;

    /**
     * Initiates a new URL reader
     *
     * @param url URL string to read from
     */
    public JsonURLReader(@NotNull URL url) {
        this.url = url;
        this.content = parseContent();
    }

    /**
     * Initiates a new URL reader
     *
     * @param url URL string to read from
     * @throws MalformedURLException If the given URL was malformed
     */
    public JsonURLReader(@NotNull String url) throws MalformedURLException {
        this(new URL(url));
    }

    /**
     * Gets the {@link JsonElement} from the URL.
     *
     * @return A JSON object of the URL content
     */
    public JsonElement getContentAsElement() {
        return content;
    }

    /**
     * Gets the JSON object from the URL.
     *
     * @return A JSON object of the URL content
     */
    public JsonObject getContentAsObject() {
        return content.getAsJsonObject();
    }

    /**
     * Returns the current JSON from the URL
     *
     * @return The content
     */
    private JsonElement parseContent() {
        try {
            return JsonUtils.getObjectFromString(IOUtils.toString(url, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new JsonParseException(e.getMessage());
        }
    }

    /**
     * Refreshes the content by re-parsing it.
     *
     * @return The new content
     */
    @Override
    public JsonElement refresh() {
        return content = parseContent();
    }

    /**
     * Writes the content to a JSON file
     *
     * @param file           File to write for
     * @param prettyPrinting Whether it should use pretty printing when writing (by adding whitespace, fixing indentation, etc.)
     * @throws IOException If IO issues were encountered while writing
     */
    public void writeToFile(@NotNull JsonFile file, boolean prettyPrinting) throws IOException {
        JsonWriter writer = new JsonWriter(file);
        writer.writeAndOverride(getContentAsElement(), prettyPrinting);
        writer.close();
    }

    /**
     * Deserializes the content to the specified object type
     *
     * @param type Type to deserialize as
     * @param <T>  Object assignment
     * @return The deserialized object
     */
    public <T> T deserializeAs(@NotNull Type type) {
        return deserializeAs(type, Gsons.DEFAULT);
    }

    /**
     * Deserializes the content to the specified object type, using the provided
     * GSON profile
     *
     * @param type Type to deserialize as
     * @param gson Gson profile to use
     * @param <T>  Object assignment
     * @return The deserialized object
     */
    public <T> T deserializeAs(@NotNull Type type, @NotNull Gson gson) {
        return gson.fromJson(content, type);
    }

    /**
     * Deserializes the object assigned to the key to the specified object type, using the provided GSON
     * profile.
     *
     * @param key  Key of the object to deserialize
     * @param type Type of the object
     * @param gson Gson profile to use
     * @param <T>  Object assignment
     * @return The deserialized object
     */
    public <T> T deserialize(@NotNull String key, @NotNull Type type, @NotNull Gson gson) {
        return gson.fromJson(content.getAsJsonObject().get(key), type);
    }

    /**
     * Deserializes the object assigned to the given key to the specified object type
     *
     * @param type Type to deserialize as
     * @param key  Key to deserialize its object
     * @param <T>  Object assignment
     * @return The deserialized object
     */
    public <T> T deserialize(@NotNull String key, Type type) {
        return deserialize(key, type, Gsons.DEFAULT);
    }

    /**
     * Returns a new {@link JsonURLReader} and throws unchecked exceptions if there were any IO exceptions
     *
     * @param url URL string to read from
     * @return The DirectConfiguration object
     */
    public static JsonURLReader of(@NotNull String url) {
        try {
            return new JsonURLReader(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

}
