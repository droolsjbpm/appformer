/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.livespark.formmodeler.renderer.client.rendering.renderers.relations.subform;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.FieldSet;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.Legend;
import org.livespark.formmodeler.model.impl.relations.SubFormFieldDefinition;
import org.livespark.formmodeler.renderer.client.rendering.FieldRenderer;
import org.livespark.formmodeler.renderer.service.FormRenderingContext;

/**
 * Created by pefernan on 9/21/15.
 */
@Dependent
public class SubFormFieldRenderer extends FieldRenderer<SubFormFieldDefinition> {

    private FieldSet container = new FieldSet();

    @Inject
    private SubFormWidget subFormWidget;

    @Override
    public void initInputWidget() {
        container.clear();
        container.add( new Legend( field.getLabel() ) );
        container.add( subFormWidget );
    }

    public IsWidget renderWidget() {
        FormGroup group = new FormGroup();
        group.getElement().setId( getFormGroupId( field ) );

        FormRenderingContext nestedContext = renderingContext.getCopyFor( field.getNestedForm(), null );
        subFormWidget.render( nestedContext );

        group.add( container );

        HelpBlock helpBlock = new HelpBlock();
        helpBlock.setId( getHelpBlokId( field ) );

        group.add( helpBlock );
        return group;
    }

    @Override
    public IsWidget getInputWidget() {
        return subFormWidget;
    }

    @Override
    public String getName() {
        return "SubForm";
    }

    @Override
    public String getSupportedFieldDefinitionCode() {
        return SubFormFieldDefinition._CODE;
    }

    public void setHtmlContent( String template ) {
        //TODO: REMOVE
    }
}
