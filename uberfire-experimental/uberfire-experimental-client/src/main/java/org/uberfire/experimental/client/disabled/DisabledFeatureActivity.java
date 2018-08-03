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

package org.uberfire.experimental.client.disabled;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.uberfire.client.mvp.AbstractWorkbenchScreenActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.experimental.client.disabled.component.DisabledFeatureComponent;

@Dependent
@Named(DisabledFeatureActivity.ID)
public class DisabledFeatureActivity extends AbstractWorkbenchScreenActivity {

    public static final String ID = "appformer.experimental.disabledFeature";

    private DisabledFeatureComponent component;

    @Inject
    public DisabledFeatureActivity(PlaceManager placeManager, DisabledFeatureComponent component) {
        super(placeManager);
        this.component = component;
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public IsWidget getWidget() {
        return ElementWrapperWidget.getWidget(component.getElement());
    }

    @Override
    public String getIdentifier() {
        return ID;
    }
}
