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
package org.uberfire.client.perspectives;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.Window;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.screens.experimental.ExperimentalPerspectiveHeader;
import org.uberfire.client.screens.experimental.ExperimentalScreen1;
import org.uberfire.client.screens.experimental.ExperimentalScreen2;
import org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelPresenter;
import org.uberfire.experimental.definition.annotations.ExperimentalFeature;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;

/**
 * A simple perspective with one tabbed panel.
 */
@ApplicationScoped
@WorkbenchPerspective(identifier = "ExperimentalPerspective")
@ExperimentalFeature(nameI18nKey = "experimental_perspective", descriptionI18nKey = "experimental_perspective_description")
public class ExperimentalPerspective {

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        final PerspectiveDefinition perspective = new PerspectiveDefinitionImpl(SimpleWorkbenchPanelPresenter.class.getName());
        perspective.setName("Experimental Perspective");

        perspective.getRoot().addPart(new PartDefinitionImpl(new DefaultPlaceRequest(ExperimentalPerspectiveHeader.ID)));

        final PanelDefinition west = new PanelDefinitionImpl(StaticWorkbenchPanelPresenter.class.getName());
        west.addPart(new PartDefinitionImpl(new DefaultPlaceRequest(ExperimentalScreen1.ID)));

        perspective.getRoot().insertChild(CompassPosition.WEST, west);

        final PanelDefinition south = new PanelDefinitionImpl(StaticWorkbenchPanelPresenter.class.getName());
        south.addPart(new PartDefinitionImpl(new DefaultPlaceRequest(ExperimentalScreen2.ID)));

        perspective.getRoot().insertChild(CompassPosition.SOUTH, south);

        return perspective;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return MenuFactory
                .newTopLevelMenu("Do nothing")
                .respondsWith(() -> Window.alert("Hello World!"))
                .position(MenuPosition.RIGHT)
                .endMenu()
                .build();
    }
}
