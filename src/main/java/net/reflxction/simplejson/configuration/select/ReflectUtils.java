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

import com.google.gson.JsonObject;
import net.reflxction.simplejson.utils.Gsons;

import java.lang.reflect.Field;

/**
 * A class with different reflection utilities
 * <p>
 * This class is not intended for public usage.
 */
public class ReflectUtils {

    public static void setStatic(Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(field.getDeclaringClass(), value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getValue(JsonObject content, String key, Field field) {
        return Gsons.DEFAULT.fromJson(content.get(key), field.getType());
    }

    public static Object getStaticValue(Field field) {
        field.setAccessible(true);
        try {
            return field.get(field.getDeclaringClass());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
