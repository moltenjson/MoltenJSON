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

import com.google.common.base.Preconditions;
import net.reflxction.simplejson.configuration.direct.DirectConfiguration;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Represents a JSON file. This is used by JSON writers and readers.
 * <p>
 * This cannot be used to write or read directly
 * For reading, {@link JsonReader} and {@link DirectConfiguration}.
 * <p>
 * For writing, {@link JsonWriter} and {@link DirectConfiguration}.
 *
 * @see JsonWriter
 * @see JsonReader
 * @see DirectConfiguration
 * @see net.reflxction.simplejson.configuration.select.SelectableConfiguration
 */
public class JsonFile {

    /**
     * Represents the bytes of a string of an empty JSON object. This will be written in empty JSON files
     * to allow them to be used by readers, writers and configurations
     */
    private static final byte[] EMPTY_JSON = "{}".getBytes();

    /**
     * The file which contains the JSON string
     */
    private final File file;

    /**
     * Represents the JSON file's path.
     */
    private final String path;

    /**
     * Initiates a JSON file
     *
     * @param file             File
     * @param createIfNotExist Whether the file should be created if it doesn't exist already.
     * @throws IOException I/O exceptions while connecting with the file
     */
    public JsonFile(File file, boolean createIfNotExist) throws IOException {
        Preconditions.checkNotNull(file, "File (file) cannot be null");
        this.file = file;
        this.path = file.getPath();
        prepare(createIfNotExist);
    }

    /**
     * Creates a new JSON file from the given parent directory and the child (JSON file) file.
     *
     * @param parent           Parent directory
     * @param child            The JSON child file
     * @param createIfNotExist Whether should the file be created if it does not exist
     *                         already.
     * @throws IOException I/O exceptions whilst connecting to the file.
     */
    public JsonFile(File parent, String child, boolean createIfNotExist) throws IOException {
        Preconditions.checkNotNull(parent, "File (parent) cannot be null");
        Preconditions.checkNotNull(child, "String (child) cannot be null");
        this.file = new File(parent, child);
        this.path = file.getPath();
        prepare(createIfNotExist);
    }

    /**
     * Creates a new JSON file from the given parent and the child file.
     *
     * @param parent           The parent directory
     * @param child            The child file. Must be a valid JSON file
     * @param createIfNotExist Whether the file should be created if it doesn't exist already.
     * @throws IOException I/O exceptions while connecting to the file
     */
    public JsonFile(String parent, String child, boolean createIfNotExist) throws IOException {
        Preconditions.checkNotNull(parent, "File (parent) cannot be null");
        Preconditions.checkNotNull(child, "String (child) cannot be null");
        this.file = new File(parent, child);
        this.path = file.getPath();
        prepare(createIfNotExist);
    }

    /**
     * {@inheritDoc}
     */
    public JsonFile(String parent, String child) throws IOException {
        this(parent, child, true);
    }

    /**
     * Retrieves a new JSON file from the given path
     *
     * @param file File to retrieve
     * @throws IOException I/O problems with connecting to the file
     */
    public JsonFile(File file) throws IOException {
        this(file, true);
    }

    /**
     * {@inheritDoc}
     */
    public JsonFile(File parent, String child) throws IOException {
        this(parent, child, true);
    }

    /**
     * Initiates a new JSON file
     *
     * @param path             Path to the JSON file
     * @param createIfNotExist Whether this should create the file if it doesn't exist already.
     * @throws IOException I/O exceptions while connecting with the file
     */
    public JsonFile(String path, boolean createIfNotExist) throws IOException {
        this(new File(path), createIfNotExist);
    }

    /**
     * Initiates a JSON file
     *
     * @param path Path of the file
     * @throws IOException I/O exceptions while connecting with the file
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
        return path;
    }

    /**
     * Prepares this {@link JsonFile} by creating it (if required), and writes
     * curly brackets ("{}") to make it usable and maintainable by readers and writers
     *
     * @param create Whether to create the file if it doesn't exist already
     * @throws IOException Issues with creating or writing
     */
    private void prepare(boolean create) throws IOException {
        if ((!file.exists() && create && file.createNewFile()) || (file.exists() &&
                isEmpty(FileUtils.readFileToString(file, StandardCharsets.UTF_8).trim())))
            writeCurlyBrackets();
    }

    /**
     * Writes curly brackets to the file
     */
    private void writeCurlyBrackets() {
        try {
            Files.write(Paths.get(file.getPath()), EMPTY_JSON); // Write the curly brackets so JSON can be parsed
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns whether this file exists or not
     *
     * @return The existence of this file
     */
    public boolean exists() {
        return file.exists();
    }

    /**
     * Whether the given String is empty or equals {@code null}
     *
     * @param text Text to check for
     * @return {@code true} if it's empty or {@code null}, false if otherwise.
     */
    private boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }

    /**
     * Retrieves a new JSON file from the given path
     *
     * @param file File to retrieve
     * @return The JsonFile object
     */
    public static JsonFile of(File file) {
        try {
            return new JsonFile(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initiates a JSON file
     *
     * @param file              File to use
     * @param createIfNotExists Whether the file should be created if it doesn't exist already.
     * @return The JsonFile object
     */
    public static JsonFile of(File file, boolean createIfNotExists) {
        try {
            return new JsonFile(file, createIfNotExists);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    /**
     * Initiates a new JSON file
     *
     * @param path             Path to the JSON file
     * @param createIfNotExist Whether this should create the file if it doesn't exist already.
     * @return The JsonFile object
     */
    public static JsonFile of(String path, boolean createIfNotExist) {
        try {
            return new JsonFile(path, createIfNotExist);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a new JSON file from the given parent directory and the child (JSON file) file.
     *
     * @param parent           Parent directory
     * @param child            The JSON child file
     * @param createIfNotExist Whether should the file be created if it does not exist
     * @return New JSON file instance
     */
    public static JsonFile of(String parent, String child, boolean createIfNotExist) {
        try {
            return new JsonFile(parent, child, createIfNotExist);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public static JsonFile of(String parent, String child) {
        try {
            return new JsonFile(parent, child);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public static JsonFile of(File parent, String child) {
        try {
            return new JsonFile(parent, child);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
