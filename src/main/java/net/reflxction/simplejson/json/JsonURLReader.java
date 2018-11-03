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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * A helping class for reading content of JSON files from URLs.
 */
public class JsonURLReader {

    // The URL to read from
    private URL url;

    /**
     * Initiates a new URL reader
     *
     * @param url URL string to read from
     * @throws MalformedURLException If the given URL was malformed
     */
    public JsonURLReader(String url) throws MalformedURLException {
        this.url = new URL(url);
    }

    /**
     * Parses all characters from the JSON file from the URL, then builds it using a {@link StringBuilder}.
     * This will help providing better iteration, as JSON files don't know "lines"
     *
     * @param reader Reader to use for reading
     * @return The JSON content of the page as a string
     * @throws IOException If IO connection issues while parsing JSON content were encountered
     */
    private String parseJsonCharacters(BufferedReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = reader.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    /**
     * Gets the JSON object from the URL.
     *
     * @return A JSON object of the URL content
     * @throws IOException Whether there were IO issues when parsing
     */
    public JSONObject readContent() throws IOException {
        try (InputStream is = url.openStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = parseJsonCharacters(reader);
            reader.close();
            return new JSONObject(jsonText);
        }
    }

    /**
     * Writes the content to a JSON file
     *
     * @param file           File to write for
     * @param prettyPrinting Whether it should use pretty printing when writing (by adding whitespace, fixing indentation, etc.)
     * @throws IOException If IO issues were encountered while writing
     */
    public void writeToFile(JsonFile file, boolean prettyPrinting) throws IOException {
        JsonWriter writer = new JsonWriter(file);
        writer.writeAndOverride(readContent(), prettyPrinting);
        writer.close();
    }

}
