/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.layout.editor.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.api.editor.LayoutInstance;
import org.uberfire.ext.layout.editor.client.components.container.Container;
import org.uberfire.ext.layout.editor.client.generator.LayoutGenerator;

@Dependent
public class LayoutEditorPresenter {

    private final View view;
    private LayoutTemplate.Style pageStyle = LayoutTemplate.Style.FLUID;
    private Container container;
    private LayoutGenerator layoutGenerator;


    @Inject
    public LayoutEditorPresenter(final View view,
                                 Container container,
                                 LayoutGenerator layoutGenerator) {
        this.view = view;
        this.container = container;
        this.layoutGenerator = layoutGenerator;
        view.init(this);
    }

    @PostConstruct
    public void initNew() {
        view.setupDesign(container.getView());
        view.setPreviewEnabled(false);
    }

    public void setPreviewEnabled(boolean previewEnabled) {
        view.setPreviewEnabled(previewEnabled);
    }

    public void clear() {
        container.reset();
    }

    public UberElement<LayoutEditorPresenter> getView() {
        return view;
    }

    public LayoutTemplate getLayout() {
        return container.toLayoutTemplate();
    }

    public void loadLayout(LayoutTemplate layoutTemplate,
                           String emptyTitleText,
                           String emptySubTitleText) {

        view.setDesignStyle(layoutTemplate.getStyle());
        container.load(layoutTemplate,
                       emptyTitleText,
                       emptySubTitleText);
    }

    public void loadEmptyLayout(String layoutName,
                                String emptyTitleText,
                                String emptySubTitleText) {
        view.setDesignStyle(pageStyle);
        container.loadEmptyLayout(layoutName,
                                  pageStyle,
                                  emptyTitleText,
                                  emptySubTitleText);
    }

    public void addLayoutProperty(String key,
                                  String value) {
        container.addProperty(key,
                              value);
    }

    public String getLayoutProperty(String key) {
        return container.getProperty(key);
    }


    public void setPageStyle(LayoutTemplate.Style pageStyle) {
        this.pageStyle = pageStyle;
    }

    public void switchToDesignMode() {
        view.setupDesign(container.getView());
    }

    public void switchToPreviewMode() {
        LayoutTemplate layoutTemplate = container.toLayoutTemplate();
        LayoutInstance layoutInstance = layoutGenerator.build(layoutTemplate);
        view.setupPreview(layoutInstance.getElement());
    }

    public interface View extends UberElement<LayoutEditorPresenter> {

        void setupDesign(UberElement<Container> container);

        void setDesignStyle(LayoutTemplate.Style pageStyle);

        void setPreviewEnabled(boolean previewEnabled);

        void setupPreview(HTMLElement previewPanel);
    }
}
