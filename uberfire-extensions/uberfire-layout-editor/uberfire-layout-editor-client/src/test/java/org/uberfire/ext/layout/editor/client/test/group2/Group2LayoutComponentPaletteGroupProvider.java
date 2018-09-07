/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.layout.editor.client.test.group2;

import java.util.Arrays;
import java.util.Collection;

import org.uberfire.ext.layout.editor.client.test.TestLayoutComponentPaletteGroupProvider;
import org.uberfire.ext.layout.editor.client.test.TestLayoutDragComponent;

public class Group2LayoutComponentPaletteGroupProvider extends TestLayoutComponentPaletteGroupProvider {

    public static final String ID = "Group2";

    public Group2LayoutComponentPaletteGroupProvider() {
        super(ID);
    }

    public Group2LayoutComponentPaletteGroupProvider(boolean defaultExpanded) {
        super(ID, defaultExpanded);
    }

    @Override
    protected Collection<TestLayoutDragComponent> getTestComponents() {
        return Arrays.asList(new Group2LayoutDragComponent1(), new Group2LayoutDragComponent2());
    }
}
