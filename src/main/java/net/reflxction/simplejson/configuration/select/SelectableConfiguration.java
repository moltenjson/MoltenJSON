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

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import net.reflxction.simplejson.json.JsonFile;
import net.reflxction.simplejson.json.JsonWriter;
import net.reflxction.simplejson.utils.Gsons;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a configuration which saves fields annotated with {@link SelectKey} to its configuration.
 * On application bootstrap, registered fields will have their value associated to their JSON value, or
 * the current field value if the JSON does not map the value yet.
 *
 * @see SelectKey
 * @see net.reflxction.simplejson.configuration.DirectConfiguration
 */
public class SelectableConfiguration {

    // JSON writer
    private final JsonWriter writer;

    // The cached JSON content on config creation
    private final JsonObject content;

    // The list which holds all registered classes
    private final Map<Class<?>, List<Field>> opted = Maps.newHashMap();

    // Whether it should include the classpath of variables when saving or not.
    private final boolean classpath;

    /**
     * Initiates a new SelectableConfiguration and assigns all fields to their values from the JSON file
     *
     * @param file      File to use
     * @param classpath Whether or not to include the variable classpath when saving
     * @throws IOException I/O exceptions while connecting with the file
     */
    public SelectableConfiguration(JsonFile file, boolean classpath) throws IOException {
        this.classpath = classpath;
        JsonFile jsonFile = new JsonFile(file.getFile());
        writer = new JsonWriter(jsonFile);
        content = writer.getCachedContent();
    }

    public SelectableConfiguration(JsonFile file) throws IOException {
        this(file, true);
    }

    /**
     * Registers the given classes and assigns all opted fields which are annotated
     * with {@link SelectKey} to their class. If the class doesn't contain any
     * field annotated with {@code @SelectKey} then this method will have no effect.
     * <p>
     * Supports varargs usage.
     *
     * @param classes Classes to register
     */
    public final void register(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            List<Field> fields = opt(clazz);
            if (fields.isEmpty()) return;
            opted.putIfAbsent(clazz, fields);
        }
    }

    /**
     * Assigns all opted fields to their JSON values.
     * <p>
     * This should be used after all the required classes have been registered,
     */
    public final void associate() {
        opted.forEach((clazz, fields) -> fields.forEach(this::assign));
    }

    /**
     * Removes the given key from the JSON file.
     * <p>
     * This will have no effect if the given key doesn't exist.
     *
     * @param key Key to remove
     */
    public final void remove(String key) {
        content.remove(key);
    }

    /**
     * Saves the configuration and updates the cached {@link #content} to have the
     * current and updated values of the fields.
     */
    public final void save() {
        try {
            opted.forEach((clazz, fields) -> fields.forEach(field -> {
                field.setAccessible(true);
                content.add(getKey(field), Gsons.DEFAULT.toJsonTree(Reflector.getStaticValue(field)));
            }));
            writer.writeAndOverride(content, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Opts all fields from the given class which are annotated with {@link SelectKey}, or returns
     * an empty {@link java.util.Collection} if the class doesn't contain any fields annotated with it.
     *
     * @param clazz Class to opt from
     * @return A {@link List} of fields from the class which are annotated with {@link SelectKey}.
     */
    private List<Field> opt(Class<?> clazz) {
        if (Arrays.stream(clazz.getDeclaredFields()).noneMatch(f -> f.isAnnotationPresent(SelectKey.class) && Modifier.isStatic(f.getModifiers())))
            return Collections.emptyList();
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(SelectKey.class))
                .collect(Collectors.toList());
    }

    /**
     * Returns the key of the field. If the field's {@code SelectKey} is empty it will return the field
     * name, otherwise it would return the parameter of {@link SelectKey}.
     *
     * @param field Field to fetch from
     * @return The field key
     */
    final String getKey(Field field) {
        if (!field.isAnnotationPresent(SelectKey.class))
            throw new RuntimeException("Found a registered key which is not annotated with @SelectKey! " + field.getDeclaringClass()
                    + "." + field.getName());
        SelectKey select = field.getAnnotation(SelectKey.class);
        String name = select.value().isEmpty() ? field.getName() : select.value();
        if (classpath || select.classpath()) {
            name = field.getDeclaringClass().getName() + "." + name;
        }
        return name;
    }

    /**
     * Assigns the field to its JSON value. If the {@link #content} doesn't contain declaration
     * for the field, it would add it to the JSON content with its current value.
     *
     * @param field Field to assign
     */
    private void assign(Field field) {
        String key = getKey(field);
        if (!content.has(key)) {
            content.add(key, Gsons.DEFAULT.toJsonTree(Reflector.getStaticValue(field)));
            return;
        }
        Object value = Reflector.getValue(this, field);
        Reflector.setStatic(field, value);
        content.add(key, Gsons.DEFAULT.toJsonTree(value));
    }

    /**
     * Returns the content of the configuration. This can be modified
     *
     * @return The configuration content
     */
    public final JsonObject getContent() {
        return content;
    }

    /**
     * Whether or not to save variables while including their classpath. If this is true,
     * variables will be saved with their classpath appended behind the variable key.
     *
     * @return Whether to use classpath or not
     */
    public final boolean isClasspath() {
        return classpath;
    }

    /**
     * Returns a new {@link SelectableConfiguration} and throws unchecked exceptions if there were any IO exceptions
     *
     * @param file File to use
     * @return The SelectableConfiguration object
     */
    public static SelectableConfiguration of(JsonFile file) {
        try {
            return new SelectableConfiguration(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initiates a new SelectableConfiguration and assigns all fields to their values from the JSON file
     *
     * @param file      File to use
     * @param classpath Whether or not to include the variable classpath when saving
     * @return The SelectableConfiguration object
     */
    public static SelectableConfiguration of(JsonFile file, boolean classpath) {
        try {
            return new SelectableConfiguration(file, classpath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
