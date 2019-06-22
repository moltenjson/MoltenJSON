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

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.reflxction.simplejson.configuration.direct.DirectConfiguration;
import net.reflxction.simplejson.json.JsonFile;
import net.reflxction.simplejson.json.JsonWriter;
import net.reflxction.simplejson.json.Lockable;
import net.reflxction.simplejson.utils.Checks;
import net.reflxction.simplejson.utils.Gsons;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a configuration which saves fields annotated with {@link SelectKey} to its configuration.
 * On application bootstrap, registered fields will have their value associated to their JSON value, or
 * the current field value if the JSON does not map the value yet.
 *
 * @see SelectKey
 * @see DirectConfiguration
 */
public class SelectableConfiguration implements Lockable<SelectableConfiguration> {

    /**
     * The JSON writer which caches the content and writes to the file
     */
    private final JsonWriter writer;

    /**
     * The cached JSON content. Initiated on configuration creation.
     */
    private JsonObject content;

    /**
     * A map which links the class with all its annotated fields
     */
    private final Map<Class<?>, List<Field>> opted = new HashMap<>();

    /**
     * Whether it should include the classpath of variables when saving or not.
     */
    private final boolean classpath;

    /**
     * Whether to allow calls to {@link #setFile(JsonFile)} or not
     */
    private final boolean locked;

    /**
     * The GSON profile to use when writing
     */
    private final Gson gson;

    /**
     * Initiates a new SelectableConfiguration and assigns all fields to their values from the JSON file
     *
     * @param file      File to use
     * @param classpath Whether or not to include the variable classpath when saving
     * @param gson      Gson profile to use
     * @param locked    Whether to allow calls to {@link #setFile(JsonFile)} or not
     * @throws IOException I/O exceptions while connecting with the file
     */
    public SelectableConfiguration(JsonFile file, boolean classpath, Gson gson, boolean locked) throws IOException {
        Checks.notNull(file);
        Checks.notNull(gson);
        this.classpath = classpath;
        JsonFile jsonFile = new JsonFile(file.getFile());
        writer = new JsonWriter(jsonFile);
        content = writer.getCachedContentAsObject();
        this.gson = gson;
        this.locked = locked;
    }

    /**
     * Initiates a new SelectableConfiguration
     *
     * @param file      JSON file to use
     * @param classpath Whether or not to include the variable classpath when saving
     * @param gson      Gson profile to use for reading and writing
     * @throws IOException I/O exceptions while connecting with the file
     */
    public SelectableConfiguration(JsonFile file, boolean classpath, Gson gson) throws IOException {
        this(file, classpath, gson, false);
    }

    /**
     * Initiates a new SelectableConfiguration with the {@link #gson} profile set to {@link Gsons#DEFAULT}
     *
     * @param file      JSON file to use
     * @param classpath Whether or not to include variable classpath when writing
     * @throws IOException I/O exceptions while connecting to the file
     */
    public SelectableConfiguration(JsonFile file, boolean classpath) throws IOException {
        this(file, classpath, Gsons.DEFAULT);
    }

    /**
     * Initiates a new SelectableConfiguration with {@link #classpath} set to {@code true}.
     *
     * @param file JSON file to use
     * @throws IOException I/O exceptions while connecting to the file
     */
    public SelectableConfiguration(JsonFile file) throws IOException {
        this(file, false, Gsons.PRETTY_PRINTING);
    }

    /**
     * Registers the given classes and assigns all opted fields which are annotated
     * with {@link SelectKey} to their class. If the class doesn't contain any
     * field annotated with {@code @SelectKey} then this method will have no effect.
     * <p>
     * Supports varargs usage.
     *
     * @param classes Classes to register
     * @return This configuration instance. This can be invoked in chained calls (for convenience)
     */
    public final SelectableConfiguration register(Class<?>... classes) {
        Preconditions.checkNotNull(classes, "Class<?>... (classes) cannot be null");
        for (Class<?> clazz : classes) {
            List<Field> fields = opt(clazz);
            if (fields.isEmpty()) return this;
            opted.putIfAbsent(clazz, fields);
        }
        return this;
    }

    /**
     * Assigns all opted fields to their JSON values.
     * <p>
     * This should be used after all the required classes have been registered,
     *
     * @return This configuration instance. This can be invoked in chained calls (for convenience)
     */
    public final SelectableConfiguration associate() {
        opted.forEach((clazz, fields) -> fields.forEach(this::assign));
        return this;
    }

    /**
     * Registers the given classes and assigns all opted fields which are annotated
     * with {@link SelectKey} to their class.
     * <p>
     * After invoking {@link #register(Class[])}, all methods will be linked (associated) with their JSON fields
     * which is done through invoking {@link #associate()}.
     *
     * @param classes Classes to register
     * @return This configuration instance. This can be invoked in chained calls (for convenience)
     * @see #register(Class[])
     * @see #associate()
     */
    public final SelectableConfiguration registerAndAssociate(Class<?>... classes) {
        register(classes);
        associate();
        return this;
    }

    /**
     * Returns whether the current component is locked or not. This will control whether
     * {@link #setFile(JsonFile)} can be used or not.
     *
     * @return Whether the current component is locked or not.
     */
    @Override
    public boolean isLocked() {
        return locked;
    }

    /**
     * Sets the new file. Implementation of this method should also update any content
     * this component controls.
     *
     * @param file New JSON file to use. Must not be null
     * @return This object instance
     */
    @Override
    public SelectableConfiguration setFile(JsonFile file) {
        checkLocked("Cannot invoke #setFile() on a locked SelectableConfiguration!");
        Checks.notNull(file);
        writer.setFile(file);
        content = writer.getCachedContentAsObject();
        return this;
    }


    /**
     * Removes the given key from the JSON file.
     * <p>
     * This will have no effect if the given key doesn't exist.
     *
     * @param key Key to remove
     */
    public final void remove(String key) {
        Checks.notNull(key);
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
                content.add(getKey(field), gson.toJsonTree(Reflector.getStaticValue(field)));
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
                    + "#" + field.getName());
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
            content.add(key, gson.toJsonTree(Reflector.getStaticValue(field)));
            return;
        }
        Object value = Reflector.getValue(this, field);
        Reflector.setStatic(field, value);
        content.add(key, gson.toJsonTree(value));
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
     * Returns a new {@link SelectableConfiguration} with the given parameters
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

    /**
     * Initiates a new SelectableConfiguration and assigns all fields to their values from the JSON file
     *
     * @param file      File to use
     * @param classpath Whether or not to include the variable classpath when saving
     * @param gson      Gson profile to use
     * @return The SelectableConfiguration object
     */
    public static SelectableConfiguration of(JsonFile file, boolean classpath, Gson gson) {
        try {
            return new SelectableConfiguration(file, classpath, gson);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a new {@link SelectableConfiguration} with the given parameters
     *
     * @param file      File to use
     * @param classpath Whether or not to include the variable classpath when saving
     * @param gson      Gson profile to use
     * @param locked    Whether to allow calls for {@link #setFile(JsonFile)} or not
     * @return The SelectableConfiguration object
     */
    public static SelectableConfiguration of(JsonFile file, boolean classpath, Gson gson, boolean locked) {
        try {
            return new SelectableConfiguration(file, classpath, gson, locked);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
