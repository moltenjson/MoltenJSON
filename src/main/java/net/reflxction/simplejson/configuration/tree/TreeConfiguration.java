/*
 * * Copyright 2019 github.com/ReflxctionDev
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
package net.reflxction.simplejson.configuration.tree;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import net.reflxction.simplejson.configuration.direct.DirectConfiguration;
import net.reflxction.simplejson.configuration.tree.strategy.TreeNamingStrategy;
import net.reflxction.simplejson.json.JsonFile;
import net.reflxction.simplejson.json.JsonWriter;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * A tree configuration is a special type of configuration, designed specifically for handling data.
 * It serves the purpose of simplifying handling data which is distributed on files rather than a single
 * file. For example, if user data was stored in a directory with each user getting their own JSON file,
 * then this configuration can handle this data efficiently.
 * <p>
 * Do note that, the files <i>need</i> to follow a specific template; i.e have a single object that can
 * be serialized and deserialized by {@link com.google.gson.Gson} rather than having to control
 * raw maps, lists, primitive types, etc. This allows the configuration to be able to handle all the
 * data efficiently.
 * <p>
 * Primitive types classes and wrapper classes (like {@link Double}), and generally any
 * class that cannot be serialized or deserialized whatsoever by {@link Gson} should <i>not</i> be used.
 * <p>
 * Collections (such as {@link List} and {@link Set}) need an empty file to be serialized
 * as a {@link com.google.gson.JsonArray}.
 * <p>
 * Maps get treated similar to {@link com.google.gson.JsonObject}, with keys being the JSON
 * keys, and values assigned to them.
 * <p>
 * Instances of {@link TreeConfiguration} should be constructed using {@link TreeConfigurationBuilder}.
 * <p>
 * A {@link TreeConfiguration} is thread-safe.
 *
 * @param <N> The file naming
 * @param <E> The file template object. This <i>must</i> be followed by ALL the files in the directory
 * @see TreeConfigurationBuilder
 * @see TreeNamingStrategy
 * @see net.reflxction.simplejson.configuration.select.SelectableConfiguration
 * @see DirectConfiguration
 * @since SimpleJSON 2.3.0
 */
@Beta
public class TreeConfiguration<N, E> {

    /**
     * Map which contains all file data. The name key is the name derived from the
     * naming strategy, and the value is the file template specified in the
     * creation of the configuration in object generics.
     */
    private Map<N, E> data = new LinkedHashMap<>();

    /**
     * The parent directory which contains all the data files
     */
    private final File directory;

    /**
     * The GSON profile used to serialize and deserialize file content.
     */
    private final Gson gson;

    /**
     * Whether to search sub-directories or not.
     */
    final boolean searchSubdirectories; // package-private to be accessed by file filter

    /**
     * Prefixes which come before the file name in order to exclude it from being loaded and updated
     */
    private final ImmutableList<String> exclusionPrefixes;

    /**
     * Extensions that files should strictly have to be loaded
     */
    private final ImmutableList<String> restrictedExtensions;

    /**
     * The naming strategy used to control the ways file names are.
     */
    private final TreeNamingStrategy<N> namingStrategy;

    /**
     * Whether or not to ignore file names that invoking {@link TreeNamingStrategy} on
     * would throw an exception, or files which cannot be parsed (malformed JSON)
     */
    private final boolean ignoreInvalidFiles;

    /**
     * The file filter for getting all the files that meet the criteria
     */
    private final TreeFileFilter<N, E> fileFilter;

    /**
     * A simple boolean to track whether the data has been loaded or not.
     */
    private boolean dataLoaded = false;

    /**
     * All data files
     */
    private List<File> files;

    /**
     * A JSON file that is used to load the content
     */
    private JsonWriter writer = null;

    /**
     * Used locally. To construct, use {@link TreeConfigurationBuilder}.
     *
     * @param directory            Directory which contains all the data files
     * @param gson                 The GSON profile used for handling JSON data
     * @param searchSubdirectories Whether or not to search sub-directories
     * @param exclusionPrefixes    Prefixes that exclude files from being loaded and updated
     * @param restrictedExtensions File extensions to exclude
     * @param namingStrategy       The naming strategy used to fetch file names and convert them into usable names
     * @param ignoreInvalidFiles   Whether or not to ignore files whom names cannot be fetched from the naming strategy,
     *                             or cannot be parsed (malformed JSON)
     */
    TreeConfiguration(File directory, Gson gson, boolean searchSubdirectories, ImmutableList<String> exclusionPrefixes, ImmutableList<String> restrictedExtensions, TreeNamingStrategy<N> namingStrategy, boolean ignoreInvalidFiles) {
        this.directory = directory;
        this.gson = gson;
        this.searchSubdirectories = searchSubdirectories;
        this.exclusionPrefixes = exclusionPrefixes;
        this.restrictedExtensions = restrictedExtensions;
        this.namingStrategy = namingStrategy;
        this.ignoreInvalidFiles = ignoreInvalidFiles;
        fileFilter = new TreeFileFilter<>(this);
        files = getIncludedFiles();
    }

    /**
     * Returns prefixes that come before a file name to exclude it from being loaded and updated
     *
     * @return Exclusion file prefixes
     */
    public ImmutableList<String> getExclusionPrefixes() {
        return exclusionPrefixes;
    }

    /**
     * Returns the allowed file extensions in order to load the file
     *
     * @return The restricted file extensions
     */
    public ImmutableList<String> getRestrictedExtensions() {
        return restrictedExtensions;
    }

    /**
     * Returns the cached data files
     *
     * @return Data file
     */
    public List<File> getFiles() {
        return files;
    }

    /**
     * Loads all the files data into memory and caches it into {@link #data}. Invoking this method
     * would update the cache.
     * <p>
     * With the provided {@link TreeNamingStrategy}, the file name would be converted to the specified
     * naming type, hence, as long as the naming strategy is valid and works, all keys should be mapped
     * accordingly and correctly.
     * <p>
     * Invoking this method updates the value {@link #dataLoaded} to be {@code true}.
     *
     * @param templateType The type of data to serialize. This should be exactly the same
     *                     as the one specified in generics declaration.
     * @return The loaded data
     * @throws IOException Any I/O errors when connecting to files.
     * @see #getData()
     */
    public Map<N, E> load(Type templateType) throws IOException {
        dataLoaded = true;
        data.clear();
        for (File file : files) {
            try {
                setFile(file, false);
                N name;
                name = namingStrategy.fromName(FilenameUtils.getBaseName(file.getName()));
                data.put(name, gson.fromJson(writer.getCachedContentAsElement(), templateType));
            } catch (Throwable throwable) {
                if (ignoreInvalidFiles) continue;
                throw throwable;
            }
        }
        return data;
    }

    /**
     * Returns all the file data, mapped according to the specified {@link TreeNamingStrategy}.
     *
     * @return The total file data
     * @see TreeConfiguration#load(Type)
     */
    public Map<N, E> getData() {
        return data;
    }

    /**
     * Returns whether the data has been loaded yet or not.
     *
     * @return Whether the data was loaded or not
     */
    public boolean isDataLoaded() {
        return dataLoaded;
    }

    /**
     * Returns whether the specified name has any data loaded. If {@link #load(Type)} was not
     * invoked, this method will always return {@code false} regardless of the name.
     *
     * @param name Name to look up
     * @return {@code true} if the specified name has any data, or {@code false} if not (or if data
     * was not loaded.)
     * @see #get(Object)
     */
    public boolean hasData(N name) {
        return dataLoaded && data.containsKey(name);
    }

    /**
     * Returns the associated data with the specified name.
     * <p>
     * Note: It is recommended to execute a null check when retrieving this value. Another alternative
     * is checking with {@link #hasData(Object)} and {@link #isDataLoaded()}.
     *
     * @param name Name to look up
     * @return The name value.
     * @see #hasData(Object)
     */
    public E get(N name) {
        return data.get(name);
    }

    /**
     * Returns whether the specified string represents an exclusion prefix
     *
     * @param prefix Prefix to check
     * @return {@code true} if the string is an exclusion prefix, and {@code false}
     * if otherwise.
     */
    public boolean isExclusionPrefix(String prefix) {
        return exclusionPrefixes.contains(prefix);
    }

    /**
     * Returns whether the specified extension is allowed in order to allow the file
     * to be loaded and updated
     *
     * @param extension Extension to check
     * @return {@code true} if the extension is allowed, and {@code false} if
     * otherwise.
     */
    public boolean isExtensionAllowed(String extension) {
        return restrictedExtensions.contains(extension);
    }

    /**
     * Maps the file into the data map, and creates a file with the specified name
     * using the naming strategy. This will also write the specified content into the file
     * and update it immediately.
     * <p>
     * If the data already exists, it will be overwritten and replaced by the specified value.
     *
     * @param name          Name to map with
     * @param value         Value of the file
     * @param fileExtension The file extension this file should have. Must be included in
     *                      the {@link #restrictedExtensions}.
     * @return The specified value of the name. This can be used as a convenient way to store
     * the value.
     * @throws IOException Any I/O exceptions in handling data
     * @see #createIfAbsent(Object, Object, String)
     */
    public E create(N name, E value, String fileExtension) throws IOException {
        data.put(name, value);
        data.put(name, value);
        Preconditions.checkArgument(restrictedExtensions.contains(fileExtension), "The specified file extension (\"" + fileExtension + "\") is not one of the allowed extensions (" + restrictedExtensions + ")");
        File file = new File(directory, namingStrategy.toName(name) + "." + fileExtension);
        files.add(file);
        setFile(file, true).writeAndOverride(get(name), gson);
        return value;
    }

    /**
     * Maps the file into the data map, and creates a file with the specified name
     * using the naming strategy. This will also write the specified content into the file
     * and update it immediately.
     * <p>
     * If the data already exists, this method will have no effect
     *
     * @param name          Name to map with
     * @param value         Value of the file
     * @param fileExtension The file extension this file should have. Must be included in
     *                      the {@link #restrictedExtensions}.
     * @return The specified value of the name. This can be used as a convenient way to store
     * the value.
     * @throws IOException Any I/O exceptions in handling data
     */
    public E createIfAbsent(N name, E value, String fileExtension) throws IOException {
        E e = get(name);
        if (e == null) {
            e = create(name, value, fileExtension);
        }
        return e;
    }

    /**
     * Removes the specified entry from the data, and returns an optional of the file associated with
     * that entry.
     *
     * @param name Name of the entry
     * @return An optional of a file that is associated to this entry
     */
    private Optional<File> removeEntry(N name) {
        E value = data.remove(name);
        if (value == null) return Optional.empty();
        return files.stream().filter(file -> FilenameUtils.getBaseName(file.getName()).equals(namingStrategy.toName(name))).findFirst();
    }

    /**
     * Deletes the specified name from the cached data map and from the files.
     *
     * @param name Name of the entry to be deleted
     * @return The deleted value of the entry, or {@code null} if it had no mapping.
     */
    public E delete(N name) {
        E value = data.remove(name);
        if (value == null) return null;
        //noinspection ResultOfMethodCallIgnored
        exclude(name).ifPresent(File::delete);
        return value;
    }

    /**
     * Removes the specified name from the data cache map and from the loaded files, but the storage file
     * remains. This can be useful when excluding a specific file with keeping the data of it as well.
     *
     * @param name Name of the entry to be removed
     * @return An optional of the file which belongs to the specified name
     */
    public Optional<File> exclude(N name) {
        Optional<File> entryFile = removeEntry(name);
        entryFile.ifPresent(files::remove);
        return entryFile;
    }

    /**
     * Saves the specified data map, and updates the cached one. This method will write the map content
     * to each file according to the naming strategy, and overwrite its old content with the new one specified in the map.
     * <p>
     * This method should be used after the data was loaded, modified and needs to be saved (e.g application shutdown).
     * <p>
     * It's extremely recommended that the passed {@code Map} is derived from {@link #getData()}. This will ensure
     * that all files are included in getting updated, and any file that is not in the map will throw
     * a {@link NullPointerException}. Any passed map should be derived from {@link #getData()}, and contain at least
     * all the keys the current {@link #data} has.
     *
     * @param data New data to save
     * @return The inputted map data
     * @throws IOException Any exceptions in I/O writing
     */
    public Map<N, E> saveNewMap(Map<N, E> data) throws IOException {
        this.data = data;
        for (File file : files) {
            setFile(file, false);
            try {
                N name = namingStrategy.fromName(FilenameUtils.getBaseName(file.getName()));
                writer.writeAndOverride(data.get(name), gson);
            } catch (Throwable throwable) {
                if (ignoreInvalidFiles) continue;
                throw throwable;
            }
        }
        return data;
    }

    /**
     * Returns all files that meet the criteria of this configuration. Invoking this method
     * updates the cached {@link #files} list.
     * <p>
     * Files are picked using {@link TreeFileFilter}.
     *
     * @return A list of all files that meet this criteria
     */
    private List<File> getIncludedFiles() {
        if (isDirectoryEmpty(directory)) return new LinkedList<>();
        File[] files = Preconditions.checkNotNull(directory.listFiles(fileFilter));
        if (files.length == 0) return new LinkedList<>();
        List<File> includedFiles = new LinkedList<>();
        for (File file : files) {
            if (!file.isDirectory())
                includedFiles.add(file);
            else if (searchSubdirectories)
                Collections.addAll(includedFiles, Preconditions.checkNotNull(file.listFiles(fileFilter)));
        }
        return this.files = includedFiles;
    }

    /**
     * A simple method to update the embedded {@link JsonFile} inside a {@link JsonWriter}.
     *
     * @param file   New file to set
     * @param create Whether or not to create it if it does not exist already.
     * @return The affected {@link JsonWriter}
     * @throws IOException I/O errors when declaring a new writer or file.
     */
    private JsonWriter setFile(File file, boolean create) throws IOException {
        if (writer == null)
            return writer = new JsonWriter(new JsonFile(file, create));
        else
            return writer.setFile(new JsonFile(file, create));
    }

    /**
     * Returns a {@link TreeConfigurationBuilder} derived from this instance
     *
     * @return The new configuration builder
     */
    public TreeConfigurationBuilder<N, E> asBuilder() {
        return new TreeConfigurationBuilder<N, E>(directory, namingStrategy)
                .setGson(gson)
                .searchSubdirectories(searchSubdirectories)
                .setExclusionPrefixes(exclusionPrefixes)
                .setRestrictedExtensions(restrictedExtensions)
                .ignoreInvalidFiles(ignoreInvalidFiles);
    }

    /**
     * Returns whether the specified directory is empty or not
     *
     * @param directory Directory to check
     * @return {@code true} if the directory is empty, {@code false} if otherwise. If
     * {@link Files#newDirectoryStream(Path)} throws an {@link IOException}, it returns {@code true} to
     * avoid making any calls to this directory.
     */
    public static boolean isDirectoryEmpty(File directory) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory.toPath())) {
            boolean empty = !stream.iterator().hasNext();
            stream.close();
            return empty;
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }
    }
}