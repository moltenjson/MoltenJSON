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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Represents a JSON file. This is used by JSON writers and readers
 */
public class JsonFile {

    // The file of the JSON file
    private File file;

    /**
     * Initiates a JSON file
     *
     * @param file             File
     * @param createIfNotExist Whether the file should be created if it doesn't exist already.
     */
    public JsonFile(File file, boolean createIfNotExist) throws IOException {
        this.file = file;
        maintain(createIfNotExist);
        if (!file.exists() && createIfNotExist) //noinspection ResultOfMethodCallIgnored
            file.createNewFile();


    }

    /**
     * Retrieves a new JSON file from the given path
     *
     * @param file File to retrieve
     */
    public JsonFile(File file) throws IOException {
        this(file, true);
    }

    /**
     * Initiates a new JSON file
     *
     * @param path             Path to the JSON file
     * @param createIfNotExist Whether this should create the file if it doesn't exist already.
     */
    public JsonFile(String path, boolean createIfNotExist) throws IOException {
        this(new File(path), createIfNotExist);
    }

    /**
     * Initiates a JSON file
     *
     * @param path Path of the file
     */
    public JsonFile(String path) throws IOException {
        this(path, true);
    }

    /**
     * The file value of the JSON file
     *
     * @return The file value of the JSON file
     */
    public File getFile() {
        return file;
    }

    /**
     * The file path
     *
     * @return The path leading to the file
     */
    public String getPath() {
        return file.getPath();
    }

    /**
     * Maintains this {@link JsonFile} by creating it (if required), and writes
     * curly brackets ("{}") to make it usable and maintainable by readers and writers
     *
     * @throws IOException Issues with creating or writing
     */
    private void maintain(boolean create) throws IOException {
        if ((!file.exists() && create && file.createNewFile()) || (file.exists() &&
                isEmpty(FileUtils.readFileToString(file, Charset.forName("UTF-8")).trim())))
            writeCurlyBrackets();
    }

    /**
     * Writes curly brackets to the file
     */
    private void writeCurlyBrackets() {
        try {
            Files.write(Paths.get(file.getPath()), "{}".getBytes()); // Write the curly brackets so JSON can be parsed
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }

    /**
     * Returns a new {@link JsonFile} and throws unchecked exceptions if there were any IO exceptions
     *
     * @param path Path of the JSON file
     * @return The JsonFile object
     */
    public static JsonFile of(String path) {
        try {
            return new JsonFile(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
