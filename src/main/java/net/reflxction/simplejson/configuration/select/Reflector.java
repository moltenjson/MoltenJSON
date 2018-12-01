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

import net.reflxction.simplejson.utils.Gsons;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

/**
 * A class with different reflection utilities
 * <p>
 * This class is not intended for public usage.
 */
class Reflector {

    static void setStatic(Field field, Object value) {
        try {
            if (field.getType().equals(SelectionHolder.class)) {
                getSelectionHolder(field).set(value);
                return;
            }
            field.setAccessible(true);
            field.set(field.getDeclaringClass(), value);
            System.out.println("non final");
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    static Object getValue(SelectableConfiguration configuration, Field field) {
        Class<?> d = field.getType();
        if (field.getType().equals(SelectionHolder.class)) {
            d = getGeneric(field);
        }
        return Gsons.DEFAULT.fromJson(configuration.getContent().get(configuration.getKey(field)), d);
    }

    static Object getStaticValue(Field field) {
        try {
            field.setAccessible(true);
            if (field.getType().equals(SelectionHolder.class)) {
                return getSelectionHolder(field).get();
            }
            return field.get(field.getDeclaringClass());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static SelectionHolder getSelectionHolder(Field field) {
        try {
            return ((SelectionHolder) field.get(null));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Class<?> getGeneric(Field field) {
        System.out.println(field.getName());
        ParameterizedType type = (ParameterizedType) field.getGenericType();
        System.out.println(type);
        return (Class<?>) type.getActualTypeArguments()[0];
    }

}