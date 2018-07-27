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
package net.reflxction.simplejson;

import com.google.gson.Gson;

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

    // A private buffered reader to handle reading and managing IO for the reader
    private BufferedReader reader;

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
    public <T> T readJson(Class<T> clazz) {
        Gson gson = new Gson();
        try {
            reader = new BufferedReader(new FileReader(getFile().getFile()));
            T result = gson.fromJson(reader, clazz);
            if (result != null) {
                return result;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Closes the IO connection between the reader and the file. This MUST be called when you are done with using the reader
     */
    public void close() {
        if (reader == null) return;
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
