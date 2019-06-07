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
package net.reflxction.simplejson.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.reflxction.simplejson.json.JsonFile;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * A simple utility to simplify checking for specific conditions
 */
public class Checks {

    public static void notNull(Gson gson) {
        Objects.requireNonNull(gson, "Gson (gson) cannot be null");
    }

    public static void notNull(String key) {
        Objects.requireNonNull(key, "String (key) cannot be null");
    }

    public static void notNull(Type type) {
        Objects.requireNonNull(type, "Type (type) cannot be null");
    }

    public static void notNull(JsonObject object) {
        Objects.requireNonNull(object, "JsonObject (object) cannot be null");
    }

    public static void notNull(JsonFile file) {
        Objects.requireNonNull(file, "JsonFile (file) cannot be null");
    }

    public static void notNull(Object o) {
        Objects.requireNonNull(o, "Object (value) cannot be null");
    }

}
