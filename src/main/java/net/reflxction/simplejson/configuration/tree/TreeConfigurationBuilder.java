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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import net.reflxction.simplejson.configuration.tree.strategy.TreeNamingStrategy;
import net.reflxction.simplejson.utils.Gsons;

import java.io.File;

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
     * Whether or not to allow excluding files.
     *
     * @see #exclusionPrefixes
     */
    private boolean exclude = false;

    /**
     * Prefixes which come before the file name in order to exclude it from being loaded and updated
     */
    private ImmutableList<String> exclusionPrefixes = EMPTY_LIST;

    /**
     * Whether or not to restrict loaded files to have a specific extension.
     *
     * @see #restrictedExtensions
     */
    private boolean restrictExtension = false;

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
     * @param directory Directory which contains all the data files
     */
    public TreeConfigurationBuilder(File directory, TreeNamingStrategy<N> namingStrategy) {
        Preconditions.checkNotNull(directory, "File (directory) cannot be null");
        Preconditions.checkArgument(directory.isDirectory(), "File is not a directory!");
        Preconditions.checkNotNull(namingStrategy, "TreeNamingStrategy<N> (namingStrategy) cannot be null");
        this.directory = directory;
        this.namingStrategy = namingStrategy;
    }

    /**
     * Sets the GSON profile used in loading and updating operations
     *
     * @param gson New GSON profile to set
     * @return This builder instance
     */
    public TreeConfigurationBuilder<N, E> setGson(Gson gson) {
        if (gson == null) return this;
        this.gson = gson;
        return this;
    }

    /**
     * Sets whether sub-directories should be searched for files
     *
     * @return This builder instance
     */
    public TreeConfigurationBuilder<N, E> searchSubdirectories() {
        return searchSubdirectories(true);
    }

    /**
     * Sets whether sub-directories should be searched for files.
     * <p>
     * Used locally by {@link TreeConfiguration#asBuilder()}.
     *
     * @param searchSubdirectories New value to set
     * @return This builder instance
     */
    TreeConfigurationBuilder<N, E> searchSubdirectories(boolean searchSubdirectories) {
        this.searchSubdirectories = searchSubdirectories;
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
     * @return This builder instance
     */
    public TreeConfigurationBuilder<N, E> setExclusionPrefixes(ImmutableList<String> prefixes) {
        if (prefixes == null) return this;
        this.exclude = !prefixes.isEmpty();
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
     * @return This builder instance
     */
    public TreeConfigurationBuilder<N, E> setRestrictedExtensions(ImmutableList<String> extensions) {
        if (extensions == null) return this;
        this.restrictExtension = !extensions.isEmpty();
        this.restrictedExtensions = extensions;
        return this;
    }

    /**
     * Sets the naming strategy used in this configuration. This is to fetch file names and update them.
     *
     * @param strategy New strategy to set
     * @return This builder instance
     */
    public TreeConfigurationBuilder<N, E> setNamingStrategy(TreeNamingStrategy<N> strategy) {
        Preconditions.checkNotNull(namingStrategy, "TreeNamingStrategy<N> (namingStrategy) cannot be null");
        this.namingStrategy = strategy;
        return this;
    }

    /**
     * Whether or not to ignore file names that invoking {@link TreeNamingStrategy#fromName(String)} on
     * throws an exception, or files which cannot be parsed (malformed JSON).
     *
     * @param ignoreInvalidFiles New value to set
     * @return This builder instance
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
        return new TreeConfiguration<>(directory, gson, searchSubdirectories, exclusionPrefixes, restrictedExtensions, namingStrategy, ignoreInvalidFiles);
    }

}
