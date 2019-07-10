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

import com.google.common.collect.ImmutableList;
import net.moltenjson.configuration.tree.strategy.TreeNamingStrategy;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileFilter;

/**
 * A file filter strategy for {@link TreeConfiguration}. This serves the purpose of excluding files based
 * on restricted extensions as well as exclusion prefixes.
 *
 * @param <N> File naming. See {@link TreeNamingStrategy}
 * @param <E> File template. See {@link TreeConfiguration}
 * @see TreeConfiguration
 * @see TreeConfigurationBuilder
 * @since SimpleJSON 2.3.0
 */
public class TreeFileFilter<N, E> implements FileFilter {

    /**
     * The exclusion prefixes
     */
    private ImmutableList<String> exclusionPrefixes;

    /**
     * The restricted file extensions
     */
    private ImmutableList<String> restrictedExtensions;

    /**
     * Whether or not to search sub-directories
     */
    private boolean searchSubdirectories;

    public TreeFileFilter(TreeConfiguration<N, E> configuration) {
        this.exclusionPrefixes = configuration.getExclusionPrefixes();
        this.restrictedExtensions = configuration.getRestrictedExtensions();
        this.searchSubdirectories = configuration.searchSubdirectories;
    }

    /**
     * Tests whether or not the specified abstract pathname should be
     * included in a pathname list.
     *
     * @param pathname The abstract pathname to be tested
     * @return <code>true</code> if and only if <code>pathname</code>
     * should be included
     */
    @Override
    public boolean accept(File pathname) {
        if (pathname.isDirectory() && searchSubdirectories) return true;
        if (exclusionPrefixes.stream().anyMatch(pathname.getName()::startsWith))
            return false;
        else return restrictedExtensions.stream().anyMatch(FilenameUtils.getExtension(pathname.getName())::equals);
    }
}
