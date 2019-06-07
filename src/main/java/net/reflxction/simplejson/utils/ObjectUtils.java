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
package net.reflxction.simplejson.utils;

import java.util.function.Consumer;

/**
 * A simple utility class for objects
 */
public class ObjectUtils {

    private ObjectUtils() {
        throw new AssertionError(ObjectUtils.class.getName() + " cannot be initiated");
    }

    /**
     * Executes the given {@link Consumer} if the object is not null
     *
     * @param object Object to check for
     * @param task   Task to execute if not null
     * @param <T>    Object reference
     * @return Referenced object
     */
    public static <T> T ifNotNull(T object, Consumer<T> task) {
        if (object != null)
            task.accept(object);
        return object;
    }

    /**
     * Executes the given {@link Runnable} if the object is null
     *
     * @param object Object to check for
     * @param task   Task to execute if null
     * @param <T>    Object reference
     * @return Referenced object
     */
    public static <T> T ifNull(T object, Runnable task) {
        if (object == null)
            task.run();
        return object;
    }

}
