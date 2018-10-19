/*
 * Copyright 2017 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.plugin.client.perspective.editor.api;

import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;

/**
 * Any class implementing this interface class is used to add an instance of {@link LayoutDragComponentGroup} to
 * the Perspective Editor's component palette.
 */
public interface PerspectiveEditorComponentGroupProvider extends Comparable {

    /**
     * Return the name of the component group displayed in the component palette.
     */
    String getName();

    /**
     * Get the {@link LayoutDragComponentGroup} containing the {@link LayoutDragComponent} instances
     * listed under the group's category in the component palette.
     */
    LayoutDragComponentGroup getInstance();

    /**
     * How important is this group in relation to other groups available. For example, more relevant groups
     * are displayed first in the component palette.
     */
    default Integer getOrder() {
        return 0;
    }

    /**
     * Determines if the provider is enabled. Only enabled {@link PerspectiveEditorComponentGroupProvider} will
     * provide the {@link LayoutDragComponentGroup} instance into the componentes palette.
     * @return
     */
    default boolean isEnabled() {
        return true;
    }

    @Override
    default int compareTo(Object o) {
        if (this == o) {
            return 0;
        }
        if (o == null) {
            return -1;
        }
        try {
            PerspectiveEditorComponentGroupProvider other = (PerspectiveEditorComponentGroupProvider) o;
            if (other.getOrder() == this.getOrder()) {
                return this.getName().compareTo(other.getName());
            }
            return this.getOrder().compareTo(other.getOrder()) * -1;
        }
        catch (ClassCastException e) {
            return -1;
        }
    }
}
