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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guvnor.structure.pom.DependencyType;
import org.guvnor.structure.pom.DynamicPomDependency;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DependencyTypesMapperTest {

    private DependencyTypesMapper mapper;

    @Before
    public void setUp() {
        mapper = new DependencyTypesMapper();
    }

    @Test
    public void mappingTest() {
        Map<DependencyType, List<DynamicPomDependency>> mapping = mapper.getMapping();
        assertThat(mapping).isNotEmpty();
        TestUtil.testJPADep(mapping);
    }

    @Test
    public void mappingDependencyTest() {
        List<DynamicPomDependency> deps = mapper.getDependencies(EnumSet.of(DependencyType.JPA));
        assertThat(deps).isNotEmpty();
        assertThat(deps).hasSize(1);
        Map<DependencyType, List<DynamicPomDependency>> map = new HashMap<>();
        map.put(DependencyType.JPA,
                deps);
        TestUtil.testJPADep(map);
    }

    @Test
    public void mappingInternalDependenciesTest() {
        Set<DynamicPomDependency> deps = mapper.getInternalArtifacts();
        assertThat(deps).isNotEmpty();
        assertThat(deps).hasSize(3);
    }
}
