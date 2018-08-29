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

package org.uberfire.experimental.client.editor.test;

import org.uberfire.experimental.service.definition.ExperimentalFeatureDefinition;
import org.uberfire.experimental.service.definition.impl.ExperimentalFeatureDefRegistryImpl;

public class TestExperimentalFeatureDefRegistry extends ExperimentalFeatureDefRegistryImpl {

    public static final String FEATURE_1 = "feature_1";
    public static final String FEATURE_2 = "feature_2";
    public static final String FEATURE_3 = "feature_3";


    public TestExperimentalFeatureDefRegistry() {
        register(new ExperimentalFeatureDefinition(FEATURE_1, false, "", FEATURE_1, FEATURE_1));
        register(new ExperimentalFeatureDefinition(FEATURE_2, false, "", FEATURE_2, FEATURE_2));
        register(new ExperimentalFeatureDefinition(FEATURE_3, false, "", FEATURE_3, FEATURE_3));
    }
}