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
package net.reflxction.simplejson.configuration.select;

/**
 * Represents a selection holder.
 * <p>
 * Selection holders are similar to constants however they allow modifications to them easily,
 * hence they can be used if the user does not wish to use raw types with {@code public static}
 * modifiers and allows the use of {@code final} modifier to use the <s>constant-like</s> pattern.
 * <p>
 * This will <s>not</s> for any object which uses generics. Specifically, this should only be used
 * with {@link String}, {@link Integer}, {@link Long}, {@link Double}, {@link Byte}, {@link Short},
 * {@link Boolean}.
 *
 * @param <T> Object type
 */
public class SelectionHolder<T> {

    // Selection value
    private T value;

    /**
     * Initiates a new selection holder from the given value
     *
     * @param value Initial value to set
     */
    public SelectionHolder(T value) {
        this.value = value;
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
        this.value = value;
    }
}
