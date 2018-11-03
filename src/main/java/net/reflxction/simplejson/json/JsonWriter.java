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
package net.reflxction.simplejson.json;

import com.google.gson.Gson;
import net.reflxction.simplejson.utils.Gsons;
import net.reflxction.simplejson.utils.JsonUtils;
import net.reflxction.simplejson.utils.Unprepared;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Writes data and content to the JSON file
 */
public class JsonWriter {

    // A buffered writer used to manage IO connections
    private BufferedWriter bufferedWriter;

    // The JSON file
    private JsonFile file;

    // The cached content of the file
    private JSONObject content;

    /**
     * Initiates a new JSON writer
     *
     * @param file JSON file to read from
     * @throws IOException If there were IO issues whilst initiating the file writer
     */
    public JsonWriter(JsonFile file) throws IOException {
        this.file = file;
        JsonReader reader = new JsonReader(file);
        content = reader.getAsJSONObject(Throwable::printStackTrace);
        reader.close();
    }

    /**
     * Initiates a new JSON writer from a {@link BufferedWriter}.
     * <p>
     * This is recommended when you want to read a resource/file that is embedded
     * inside your project resources
     *
     * @param writer Writer to initiate from
     */
    public JsonWriter(BufferedWriter writer) {
        bufferedWriter = writer;
    }

    /**
     * Writes the given content to the JSON file
     * <p>
     * You must keep in mind that you shouldn't call any methods like getName() when you want to save an object.
     * Using getters in this purpose
     * <p>
     * This method will override the entire content of the file, so if you want to add a key rather than overriding
     * everything, using {@link #add(String, Object, boolean, boolean)} would be more appropriate.
     *
     * @param jsonResult     JSON object to be saved
     * @param prettyPrinting Whether the writer should write it in a pretty manner
     * @throws IOException if it encountered IO issues while writing
     */
    public void writeAndOverride(Object jsonResult, boolean prettyPrinting) throws IOException {
        Gson gson = prettyPrinting ? Gsons.PRETTY_PRINTING : Gsons.DEFAULT;
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
     * @param override       Whether to override the key value if it exists already or not
     * @throws IOException If the {@link BufferedWriter} encounters any I/O issues
     */
    public void add(String key, Object value, boolean prettyPrinting, @Unprepared boolean override) throws IOException {
        //if (valueExists(key) && !override) return;
        content.put(key, value);
        write(prettyPrinting ? JsonUtils.setPretty(content.toString()) : content.toString());
    }

    /**
     * Assigns the given element to the key, and writes it to the JSON file.
     * <p>
     * This will only add the element. If you want to override the content of the file, use {@link #writeAndOverride(Object, boolean)}
     * <p>
     * This will <strong>NOT</strong> override the value if it already exists. If you want to
     * override and set it regardless, use {@link #add(String, Object, boolean, boolean)}
     *
     * @param key            Key to assign to
     * @param value          Value to assign to the key
     * @param prettyPrinting Whether to write the file in a prettified format
     * @throws IOException If the {@link BufferedWriter} encounters any I/O issues
     */
    public void add(String key, Object value, boolean prettyPrinting) throws IOException {
        add(key, value, prettyPrinting, false);
    }

    /**
     * Assigns the given element to the key, and writes it to the JSON file.
     * <p>
     * This will only add the element. If you want to override the content of the file, use {@link #writeAndOverride(Object, boolean)}
     * <p>
     * This will <strong>NOT</strong> override the value if it already exists. If you want to
     * override and set it regardless, use {@link #add(String, Object, boolean, boolean)}
     * <p>
     * This will <strong>NOT</strong> write the file in prettified format. If you want to
     * write pretty content, use {@link #add(String, Object, boolean)}
     *
     * @param key   Key to assign to
     * @param value Value to assign to the key
     * @throws IOException If the {@link BufferedWriter} encounters any I/O issues
     */
    public void add(String key, Object value) throws IOException {
        add(key, value, false);
    }

    /**
     * Removes the given key from the JSON file.
     * <p>
     * This will have no effect if the given key doesn't exist.
     *
     * @param key            Key to remove
     * @param prettyPrinting Whether to use pretty printing when writing
     * @throws IOException If the {@link BufferedWriter} encounters any I/O issues
     */
    public void removeKey(String key, boolean prettyPrinting) throws IOException {
        //if (valueExists(key)) {
        content.remove(key);
        write(prettyPrinting ? JsonUtils.setPretty(content.toString()) : content.toString());
        //}
    }

    /**
     * Removes the given key from the JSON file.
     * <p>
     * This will have no effect if the given key doesn't exist.
     * <p>
     * This will <strong>NOT</strong> use pretty printing when writing.
     *
     * @param key Key to remove
     * @throws IOException If the {@link BufferedWriter} encounters any I/O issues
     */
    public void removeKey(String key) throws IOException {
        removeKey(key, false);
    }

    /**
     * Closes the IO connection between the writer and the file. This MUST be called when you are done with using the writer
     *
     * @throws IOException If it encountered IO issues while closing
     */
    public void close() throws IOException {
        if (bufferedWriter != null)
            bufferedWriter.close();
    }

    /**
     * Returns {@code true} if the given key exists, or {@code false} if otherwise. Avoid
     * using {@link JSONObject#get(String)} as an existence check (by comparing to null), as it will
     * erase the entire content of the file, so using this should be a safer way to check if any value exists.
     *
     * @param key Key to check for
     * @return true if the value exists, false if otherwise.
     */
    public boolean valueExists(String key) {
        try {
            content.get(key);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

    /**
     * Returns the cached content which is created whenever a {@link JsonWriter} is initiated.
     * <p>
     * Mutative methods like {@link #add(String, Object)}, {@link #removeKey(String, boolean)} update
     * the content of the cached content, and write to the file (without saving).
     *
     * @return The cached JSON object
     */
    public JSONObject getCachedContent() {
        return content;
    }

    /**
     * A quick method to write text in UTF-8 charset format
     *
     * @param text Text to write
     * @throws IOException If I/O exceptions were encountered
     */
    private void write(String text) throws IOException {
        if (bufferedWriter != null) bufferedWriter.write(text);
        else Files.write(Paths.get(file.getPath()), text.getBytes());
    }

}
