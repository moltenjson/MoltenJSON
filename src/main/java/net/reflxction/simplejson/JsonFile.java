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

import net.reflxction.simplejson.exceptions.InvalidFilePathException;

import java.io.File;
import java.io.IOException;

/**
 * Represents a JSON file. This is used by JSON writers and readers
 */
public class JsonFile {

    // The file of the JSON file
    private File file;

    /**
     * Initiates a new JSON file
     *
     * @param path             Path to the JSON file
     * @param createIfNotExist Whether this should create the file if it doesn't exist already.
     *                         If true, {@link net.reflxction.simplejson.exceptions.InvalidFilePathException} will not be thrown.
     */
    public JsonFile(String path, boolean createIfNotExist) throws IOException {
        this.file = new File(path);
        if (!file.exists()) {
            if (createIfNotExist) {
                file.createNewFile();
            } else {
                throw new InvalidFilePathException("The given path " + path + " is invalid or not found.");
            }
        }
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

}
