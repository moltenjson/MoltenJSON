/*
 * * Copyright 2018-2019 github.com/ReflxctionDev
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

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * A simple class which contains different {@link java.lang.reflect.Type}s used by the library for
 * serializing and deserializing purposes.
 */
public class ReflectiveTypes {

    private ReflectiveTypes() {
        throw new AssertionError(ReflectiveTypes.class.getName() + " cannot be initiated!");
    }

    /**
     * Represents a reflective {@link java.lang.reflect.ParameterizedType} for List(e = Object) generics
     */
    public static final Type LIST_TYPE = new TypeToken<List<Object>>() {
    }.getType();

    /**
     * Represents a reflective {@link java.lang.reflect.ParameterizedType} for List(e = String) generics
     */
    public static final Type LIST_STRING_TYPE = new TypeToken<List<String>>() {
    }.getType();

    /**
     * Represents a reflective {@link java.lang.reflect.ParameterizedType} for Map(k = String, v = Object)
     */
    public static final Type MAP_TYPE = new TypeToken<LinkedHashMap<String, Object>>() {
    }.getType();

    /**
     * Represents a reflective {@link Class} for {@link JsonElement}
     */
    public static final Type ELEMENT_TYPE = JsonElement.class;

}
