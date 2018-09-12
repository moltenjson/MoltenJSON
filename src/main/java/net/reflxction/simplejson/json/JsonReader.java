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
import net.reflxction.simplejson.exceptions.JsonParseException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Reads and parses JSON data from JSON files
 */
public class JsonReader {

    // The JSON file that it should read for
    private JsonFile file;

    // A buffered reader to handle reading and managing IO for the reader, while using GSON to parse
    private BufferedReader bufferedReader;

    // A file reader, used by the buffered reader
    private FileReader fileReader;

    /**
     * Initiates a new JSON file reader
     *
     * @param file File to read for
     */
    public JsonReader(JsonFile file) {
        this.file = file;
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
     * E.g: <code>Team team = reader.readJson(Team.class);
     * for (Player player : team.getPlayers()) {
     * System.out.println(player.getName());
     * System.out.println(player.getAge());
     * }</code>
     * After the reader finishes reading, you <strong>must</strong> call {@link JsonReader#close()} to close the IO connection.
     * This is to avoid IO issues and ensures safety for the file and the JVM
     *
     * @param clazz Class which contains the object
     * @param <T>   The given object assignment
     * @return The object assigned, after parsing from JSON
     */
    public <T> T readJson(Class<T> clazz) throws JsonParseException {
        Gson gson = new Gson();
        try {
            fileReader = new FileReader(file.getFile());
            bufferedReader = new BufferedReader(fileReader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        T result = gson.fromJson(bufferedReader, clazz);
        if (result == null)
            throw new JsonParseException("Could not parse JSON from file " + getFile().getPath() + ". Object to parse: " + clazz.getName());
        return result;

    }

    /**
     * Closes the IO connection between the reader and the file. This MUST be called when you are done with using the reader
     * to avoid wasting the finite resources.
     */
    public void close() throws IOException {
        if (bufferedReader == null || fileReader == null) throw new IllegalStateException("Attempted to close a writer of an invalid JSON file!");
        bufferedReader.close();
        fileReader.close();
    }
}
