/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.structure.backend.pom;

import java.util.Set;

import org.guvnor.structure.pom.DependencyType;
import org.guvnor.structure.pom.DynamicPomDependency;
import org.uberfire.backend.vfs.Path;
/**
 * Behaviours of the PomEditor to add a single or a list of dependencies on a specified pom
 */
public interface PomEditor {

    boolean addDependency(DynamicPomDependency dep,
                          Path pomPath);

    boolean addDependencies(Set<DependencyType> dependencyTypes,
                            Path pomPath);
}
