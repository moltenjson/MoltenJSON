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

import com.google.gson.JsonObject;
import net.reflxction.simplejson.exceptions.JsonParseException;
import net.reflxction.simplejson.utils.Gsons;
import net.reflxction.simplejson.utils.JsonUtils;
import net.reflxction.simplejson.utils.ObjectUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Consumer;

/**
 * Reads and parses JSON data from JSON files
 */
public class JsonReader implements Closeable {

    // The JSON file that it should read for
    private JsonFile file;

    // A buffered reader to handle reading and managing IO for the reader, while using GSON to parse
    private BufferedReader bufferedReader;

    // A file reader, used by the buffered reader
    private FileReader fileReader;

    // Whether the reader should use the given BufferedReader instead of initiating its own
    private final boolean inputReader;

    /**
     * Initiates a new JSON file reader
     *
     * @param file File to read for
     * @throws IOException I/O exceptions while connecting with the file
     */
    public JsonReader(JsonFile file) throws IOException {
        inputReader = false;
        this.file = file;
        fileReader = new FileReader(file.getFile());
        bufferedReader = new BufferedReader(fileReader);
    }

    /**
     * Initiates a new JSON writer from a {@link BufferedReader}.
     * <p>
     * This is recommended when you want to read a resource/file that is embedded
     * inside your project resources
     *
     * @param reader Reader to initiate from
     */
    public JsonReader(BufferedReader reader) {
        inputReader = true;
        bufferedReader = reader;
    }

    /**
     * The JSON file
     *
     * @return The JSON file
     */
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
     * @param <T>   The given object assignment
     * @return The object assigned, after parsing from JSON
     */
    public <T> T deserializeAs(Type type) {
        try {
            if (!inputReader) {
                fileReader = new FileReader(file.getFile());
                bufferedReader = new BufferedReader(fileReader);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        T result = Gsons.DEFAULT.fromJson(bufferedReader, type);
        ObjectUtils.ifNull(result, () -> {
            throw new JsonParseException("Could not parse JSON from file " + getFile().getPath() + ". Object to parse: " + type.getTypeName());
        });
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
     * @param clazz   Class to deserialize as and create an instance from.
     * @param <T>     The given object assignment
     * @return The deserialized object instance
     */
    public <T> T deserialize(String element, Class<T> clazz) {
        JsonObject object = getJsonObject();
        return Gsons.DEFAULT.fromJson(object.get(element), clazz);
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
    public JsonObject getJsonObject(Consumer<IOException> onError) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(file.getPath()));
            String json = new String(encoded, StandardCharsets.UTF_8);
            return JsonUtils.getObjectFromString(json);
        } catch (IOException e) {
            ObjectUtils.ifNotNull(onError, x -> onError.accept(e));
            return null;
        }
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
        if (inputReader) {
            bufferedReader.close();
        } else {
            bufferedReader.close();
            fileReader.close();
        }
    }

    /**
     * Returns a new {@link JsonReader} and throws unchecked exceptions if there were any IO exceptions
     *
     * @param file JSON file to read from
     * @return The JsonReader object
     */
    public static JsonReader of(JsonFile file) {
        try {
            return new JsonReader(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
