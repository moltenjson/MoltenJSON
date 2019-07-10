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

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * A UUID-based naming strategy. Accessible from {@link TreeNamingStrategy#UUID_STRATEGY}.
 * <p>
 * This strategy works by converting the file name to a UUID and vice versa using
 * regular {@link UUID} methods. This can be useful when file names represent UUIDs.
 *
 * @see TreeNamingStrategy
 * @see StringNamingStrategy
 * @since SimpleJSON 2.3.0
 */
class UUIDNamingStrategy implements TreeNamingStrategy<UUID> {

    /**
     * Converts the specified object to be a valid file name. The returned file name
     * should NOT contain the extension.
     *
     * @param e Object to convert
     * @return The valid file name.
     */
    @Override
    public String toName(@NotNull UUID e) {
        return e.toString();
    }

    /**
     * Converts the file name to be an object, can be used as a key.
     *
     * @param name The file name. This does <i>NOT</i> include the extension.
     * @return The object key
     */
    @Override
    public UUID fromName(@NotNull String name) {
        return UUID.fromString(name);
    }
}
