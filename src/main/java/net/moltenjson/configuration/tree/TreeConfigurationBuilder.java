/*
 * * Copyright 2019 github.com/moltenjson
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
package net.moltenjson.configuration.tree;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import net.moltenjson.configuration.tree.strategy.TreeNamingStrategy;
import net.moltenjson.utils.Gsons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * A builder-styled class designed to construct {@link TreeConfiguration} objects
 *
 * @param <N> File naming
 * @param <E> The file template object. See {@link TreeConfiguration}
 * @see TreeConfiguration
 * @since SimpleJSON 2.3.0
 */
public class TreeConfigurationBuilder<N, E> {

    /**
     * A constant representing an empty {@link ImmutableList}.
     */
    private static final ImmutableList<String> EMPTY_LIST = ImmutableList.of();

    /**
     * The parent directory which contains all the data files
     */
    private File directory;

    /**
     * The GSON profile used to load and update data in this configuration
     */
    private Gson gson = Gsons.PRETTY_PRINTING;

    /**
     * Whether to search sub-directories or not.
     * <p>
     * Controlled by {@link #searchSubdirectories()}
     */
    private boolean searchSubdirectories = false;

    /**
     * Whether to load and save data only when requested (recommended for big storage).
     * <p>
     * Controlled by {@link #setLazy(boolean)}
     */
    private boolean lazy = false;

    /**
     * The data map. This is {@link java.util.HashMap} by default.
     * <p>
     * Controlled by {@link #setDataMap(Map)}
     */
    private Map<N, E> dataMap = new HashMap<>();

    /**
     * Prefixes which come before the file name in order to exclude it from being loaded and updated
     */
    private ImmutableList<String> exclusionPrefixes = EMPTY_LIST;

    /**
     * Extensions that files should strictly have to be loaded
     */
    private ImmutableList<String> restrictedExtensions = EMPTY_LIST;

    /**
     * The naming strategy used by this configuration
     */
    private TreeNamingStrategy<N> namingStrategy;

    /**
     * Whether or not to ignore file names that invoking {@link TreeNamingStrategy#fromName(String)} on
     * would throw exceptions, or cannot be parsed by JSON
     */
    private boolean ignoreInvalidFiles;

    /**
     * Initiates a new {@link TreeConfigurationBuilder} which controls data
     * in the specified directory.
     *
     * @param directory      Directory which contains all the data files
     * @param namingStrategy The naming strategy used in fetching file names and vice versa
     */
    public TreeConfigurationBuilder(@NotNull File directory, @NotNull TreeNamingStrategy<N> namingStrategy) {
        Preconditions.checkArgument(directory.isDirectory(), "File is not a directory!");
        this.directory = directory;
        this.namingStrategy = namingStrategy;
    }

    /**
     * Sets the GSON profile used in loading and updating operations
     *
     * @param gson New GSON profile to set
     * @return A reference to this builder
     */
    public TreeConfigurationBuilder<N, E> setGson(@Nullable Gson gson) {
        if (gson == null) return this;
        this.gson = gson;
        return this;
    }

    /**
     * Sets whether sub-directories should be searched for files
     *
     * @return A reference to this builder
     */
    public TreeConfigurationBuilder<N, E> searchSubdirectories() {
        return searchSubdirectories(true);
    }

    /**
     * Sets whether the configuration should only load data when requested (using {@link TreeConfiguration#lazyLoad(Object, Type)}),
     * and saves only the loaded entries.
     * <p>
     * Setting this is not necessary even when lazy methods are used, however it is strongly recommended
     * as it restrains access to non-lazy-safe methods such as {@link TreeConfiguration#save()}.
     *
     * @param lazy New value to set
     * @return A reference to this builder
     */
    public TreeConfigurationBuilder<N, E> setLazy(boolean lazy) {
        this.lazy = lazy;
        return this;
    }

    /**
     * Sets whether sub-directories should be searched for files.
     * <p>
     * Used locally by {@link TreeConfiguration#asBuilder()}.
     *
     * @param searchSubdirectories New value to set
     * @return A reference to this builder
     */
    TreeConfigurationBuilder<N, E> searchSubdirectories(boolean searchSubdirectories) {
        this.searchSubdirectories = searchSubdirectories;
        return this;
    }

    /**
     * Sets the map that the data would be stored in. This is by default an empty {@link java.util.HashMap},
     * however it can be changed to be another map type, allowing to choose a more appropriate map depending
     * on the environment (e.g a multithreaded environment may need a {@link java.util.concurrent.ConcurrentHashMap}).
     * <p>
     * If the passed {@code Map} is {@code null}, this method will have no effect
     *
     * @param map New map to set
     * @return A
     */
    public TreeConfigurationBuilder<N, E> setDataMap(@Nullable Map<N, E> map) {
        if (map == null) return this;
        this.dataMap = map;
        return this;
    }

    /**
     * Sets the prefixes that come before a file name to exclude it from being loaded and updated.
     * For example, if one of the exclusion prefixes is "-", any file name that starts with "-"
     * will be excluded from being loaded and updated.
     * <p>
     * Passing {@code null} will have no effect.
     *
     * @param prefixes Prefixes that exclude files
     * @return A reference to this builder
     * @throws IllegalArgumentException If the inputted list is empty
     */
    public TreeConfigurationBuilder<N, E> setExclusionPrefixes(@Nullable ImmutableList<String> prefixes) {
        if (prefixes == null) return this;
        Preconditions.checkArgument(prefixes.size() > 0, "Exclusion prefixes must contain at least one element!");
        this.exclusionPrefixes = prefixes;
        return this;
    }

    /**
     * Sets the file extensions that files must have in order to be loaded and updated
     * For example, if the restricted extensions are ["json"], any file with any extension that is not
     * "json" will be excluded from being loaded and updated.
     * <p>
     * Passing {@code null} will have no effect.
     *
     * @param extensions Restricted extensions.
     * @return A reference to this builder
     * @throws IllegalArgumentException If the inputted list is empty
     */
    public TreeConfigurationBuilder<N, E> setRestrictedExtensions(@Nullable ImmutableList<String> extensions) {
        if (extensions == null) return this;
        Preconditions.checkArgument(extensions.size() > 0, "Restricted extensions must contain at least one element!");
        this.restrictedExtensions = extensions;
        return this;
    }

    /**
     * Sets the naming strategy used in this configuration. This is to fetch file names and update them.
     *
     * @param strategy New strategy to set
     * @return A reference to this builder
     */
    public TreeConfigurationBuilder<N, E> setNamingStrategy(@Nullable TreeNamingStrategy<N> strategy) {
        this.namingStrategy = strategy;
        return this;
    }

    /**
     * Whether or not to ignore file names that invoking {@link TreeNamingStrategy#fromName(String)} on
     * throws an exception, or files which cannot be parsed (malformed JSON).
     *
     * @param ignoreInvalidFiles New value to set
     * @return A reference to this builder
     */
    public TreeConfigurationBuilder<N, E> ignoreInvalidFiles(boolean ignoreInvalidFiles) {
        this.ignoreInvalidFiles = ignoreInvalidFiles;
        return this;
    }

    /**
     * Constructs a {@link TreeConfiguration} from this builder
     *
     * @return The constructed configuration
     */
    public TreeConfiguration<N, E> build() {
        return new TreeConfiguration<>(dataMap, directory, gson, searchSubdirectories, exclusionPrefixes, restrictedExtensions, namingStrategy, ignoreInvalidFiles, lazy);
    }

}
