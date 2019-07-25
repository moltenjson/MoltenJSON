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
package net.moltenjson.json;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a component which can be locked.
 * <p>
 * Locked components do not allow access to {@link #setFile(JsonFile)}, which
 * is used to change the file this component controls, and updates any content this
 * component uses.
 * <p>
 * Any component that implements {@link Lockable} should have the controlling {@code boolean} set
 * as immutable (final). This boolean should be only controlled from a constructor and not
 * have any setters.
 *
 * @param <T> This object reference
 * @see Refreshable
 * @since SimpleJSON 2.0.2
 */
public interface Lockable<T extends Lockable> {

    /**
     * Returns whether the current component is locked or not. This will control whether
     * {@link #setFile(JsonFile)} can be used or not.
     *
     * @return Whether the current component is locked or not.
     */
    boolean isLocked();

    /**
     * Returns the {@link JsonFile} that this component controls
     *
     * @return The JsonFile that this component holds
     */
    JsonFile getFile();

    /**
     * Sets the new file. Implementation of this method should also update any content
     * this component controls.
     *
     * @param file New JSON file to use. Must not be null
     * @return This object instance
     */
    T setFile(@NotNull JsonFile file);

    /**
     * Checks whether it is safe to use {@link #setFile(JsonFile)} or not.
     *
     * @param message Message to throw if not allowed.
     */
    default void checkLocked(String message) {
        Preconditions.checkState(!isLocked(), message);
    }

}
