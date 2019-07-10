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
package net.moltenjson.configuration.tree.strategy;

import net.moltenjson.configuration.tree.TreeConfiguration;
import net.moltenjson.configuration.tree.TreeConfigurationBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents a custom naming strategy. This works as a medium to map file names appropriately
 * according to how it is implemented.
 * <p>
 * Any instances of {@link TreeNamingStrategy} should be constants, to avoid unnecessarily
 * initiating new strategy every time.
 *
 * @param <N> Naming object type
 * @see TreeConfiguration
 * @see TreeConfigurationBuilder
 * @since SimpleJSON 2.3.0
 */
public interface TreeNamingStrategy<N> {

    /**
     * Represents a simple string naming strategy
     */
    TreeNamingStrategy<String> STRING_STRATEGY = new StringNamingStrategy();

    /**
     * Represents a UUID based naming strategy
     */
    TreeNamingStrategy<UUID> UUID_STRATEGY = new UUIDNamingStrategy();

    /**
     * Converts the specified object to be a valid file name. The returned file name
     * should NOT contain the extension.
     *
     * @param e Object to convert
     * @return The valid file name.
     */
    String toName(@NotNull N e);

    /**
     * Converts the file name to be an object, can be used as a key.
     *
     * @param name The file name. This does <i>NOT</i> include the extension.
     * @return The object key
     */
    N fromName(@NotNull String name);

}
