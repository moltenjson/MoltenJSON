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
package net.moltenjson.json;

/**
 * Represents a component (such as a configuration) which can be refreshed to have its [cached] content updated. By
 * invoking {@link #refresh()} the component will have its content derived from the file.
 * <p>
 * By default, invoking {@link #refresh()} will ensure that the current (possibly modified) content in the component is destroyed,
 * and replaced by whatever is in the file.
 *
 * @param <T> The appropriate object generic to return when the component is refreshed. This can be the same object
 *            implementing this interface if a builder / chained method style is desired, or can be a {@code JsonElement} or one of
 *            its subclasses it is desired that the new content is accessed directly.
 * @see Lockable
 * @since MoltenJSON 2.4.5
 */
public interface Refreshable<T> {

    /**
     * Updates the cached content with with whatever is found in the file.
     *
     * @return The appropriate object to return when the component is refreshed. See above
     */
    T refresh();

}
