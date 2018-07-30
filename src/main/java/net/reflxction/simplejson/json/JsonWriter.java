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
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Writes data and content to the JSON file
 */
public class JsonWriter {

    // A private instance of the writer used to manage IO connections
    private FileWriter writer;

    /**
     * Initiates a new JSON writer
     *
     * @param file JSON file to read from
     * @throws IOException If there were IO issues whilst initiating the file writer
     */
    public JsonWriter(JsonFile file) throws IOException {
        this.writer = new FileWriter(file.getFile());
    }

    /**
     * Writes the given content to the JSON file
     * <p>
     * Note that each value you wish to write must be annotated with {@link com.google.gson.annotations.SerializedName}
     * and {@link com.google.gson.annotations.Expose} in order to be added to the file.
     * <p>
     * Another important thing is that you should <strong>NOT</strong> use primitive types! Use the wrapper class instead.
     * <p>
     * E.g: "@SerializedName("age") @Expose private Integer age;"
     * <p>
     * NOT "@SerializedName("age") @Expose private int age;"
     * <p>
     * You must keep in mind that you shouldn't call any methods like getName() when you want to save a string.
     * As long as the name field has the above mentioned annotations, GSON will handle saving the strings and other data
     * appropriately.
     *
     * @param jsonResult     JSON object to be saved
     * @param prettyPrinting Whether the writer should write it in a pretty manner
     * @throws IOException if it encountered IO issues while writing
     */
    public void write(Object jsonResult, boolean prettyPrinting) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        if (prettyPrinting) builder.setPrettyPrinting();
        Gson gson = builder.create();
        String json = gson.toJson(jsonResult);
        writer.write(json);
    }

    /**
     * Closes the IO connection between the writer and the file. This MUST be called when you are done with using the writer
     *
     * @throws IOException If it encountered IO issues while closing
     */
    public void close() throws IOException {
        if (writer == null) throw new IllegalStateException("Attempted to close a writer of an invalid JSON file!");
        writer.close();
    }
}
