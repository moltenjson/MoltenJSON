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
package net.moltenjson.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A simple class which contains various constants of GSON profiles
 */
public class Gsons {

    /* Cannot be initiated */
    private Gsons() {
        throw new AssertionError(Gsons.class.getName() + " cannot be initiated");
    }

    /**
     * Represents an unmodified {@link Gson} profile.
     */
    public static final Gson DEFAULT = new GsonBuilder().create();

    /**
     * Represents a {@link Gson} profile which does pretty printing when serializing (by fixing
     * indentation, etc.)
     */
    public static final Gson PRETTY_PRINTING = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Represents a {@link Gson} profile which allows serializing null values when converting a Java
     * object to a JSON string.
     */
    public static final Gson SERIALIZE_NULLS = new GsonBuilder().serializeNulls().create();

    /**
     * Represents a {@link Gson} profile which allows serializing special float points.
     */
    public static final Gson SERIALIZE_SPECIAL_FLOATING_POINT_VALUES = new GsonBuilder().serializeSpecialFloatingPointValues().create();

}
