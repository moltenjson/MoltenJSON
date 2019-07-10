/*
 * * Copyright 2019 github.com/moltenjson
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
package net.moltenjson.exceptions;

import net.moltenjson.configuration.tree.TreeConfiguration;
import net.moltenjson.configuration.tree.TreeConfigurationBuilder;
import net.moltenjson.configuration.tree.strategy.TreeNamingStrategy;

import java.io.File;

/**
 * Thrown when a {@link TreeConfiguration} attempts
 * to load or save a file which cannot be parsed by {@link com.google.gson.Gson}, or when
 * the specified {@link TreeNamingStrategy} throws
 * an exception when trying to invoke {@code fromName}.
 * <p>
 * If {@link TreeConfigurationBuilder#ignoreInvalidFiles(boolean)} is set to {@code true},
 * this exception will not be thrown
 */
public class InvalidFileException extends RuntimeException {

    /**
     * Represents the file which cannot be parsed.
     */
    private File file;

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param cause   Parent exception cause
     * @param file    The invalid file that caused the exception
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public InvalidFileException(Throwable cause, String message, File file) {
        super(message, cause);
        this.file = file;
    }

    /**
     * Returns the file which cannot be parsed
     *
     * @return The file which cannot be parsed
     */
    public File getFile() {
        return file;
    }
}
