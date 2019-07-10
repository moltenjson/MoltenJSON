/*
 * * Copyright 2018-2019 github.com/moltenjson
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
package net.moltenjson.configuration.select;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a selection holder.
 * <p>
 * Selection holders are similar to constants however they allow modifications to them easily,
 * hence they can be used if the user does not wish to use raw types with {@code public static}
 * modifiers and allows the use of {@code final} modifier to use the <i>constant-like</i> pattern.
 * <p>
 * Using this will be more encouraged than using raw types, as this supports <i>thread-safety</i>, unlike
 * the raw types which lack the {@code final} modifier, hence possibly <i>non-thread-safe</i>.
 *
 * @param <T> Object type
 * @see SelectableConfiguration
 * @see SelectKey
 */
public class SelectionHolder<T> {

    /**
     * The value wrapped inside this selection holder
     */
    private T value;

    /**
     * Whether should this holder allow null values or not
     */
    private boolean allowNullValues;

    /**
     * Initiates a new selection holder from the given value, and sets whether the given value may
     * be null or not.
     *
     * @param value           Initial value to set
     * @param allowNullValues Whether or not to allow null values
     */
    public SelectionHolder(T value, boolean allowNullValues) {
        this.allowNullValues = allowNullValues;
        this.value = allowNullValues ? value : Preconditions.checkNotNull(value, "Value in the target SelectionHolder may not be null.");
    }

    /**
     * Initiates a new selection holder from the given value
     *
     * @param value Initial value to set. This may be null
     */
    public SelectionHolder(@Nullable T value) {
        this(value, true);
    }

    /**
     * Returns he value of the holder
     *
     * @return The value
     */
    public T get() {
        return value;
    }

    /**
     * Sets the value embedded in the holder
     *
     * @param value New value to set
     */
    public void set(T value) {
        this.value = allowNullValues ? value : Preconditions.checkNotNull(value, "Value in the target SelectionHolder may not be null.");
    }

    @Override
    public String toString() {
        return "SelectionHolder{" +
                "value=" + value +
                ", allowNullValues=" + allowNullValues +
                '}';
    }
}
