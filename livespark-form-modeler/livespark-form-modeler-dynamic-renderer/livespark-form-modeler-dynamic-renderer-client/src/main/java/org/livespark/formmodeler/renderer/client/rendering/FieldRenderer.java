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
package org.livespark.formmodeler.renderer.client.rendering;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.livespark.formmodeler.model.FieldDefinition;
import org.livespark.formmodeler.renderer.service.FormRenderingContext;
import org.livespark.formmodeler.rendering.client.view.validation.FormViewValidator;

/**
 * Created by pefernan on 9/21/15.
 */
public abstract class FieldRenderer<F extends FieldDefinition> {

    protected FormRenderingContext renderingContext;
    protected F field;

    public void init( FormRenderingContext renderingContext, F field ) {
        this.renderingContext = renderingContext;
        this.field = field;
        initInputWidget();
    }

    public IsWidget renderWidget() {
        FormGroup group = new FormGroup();
        group.getElement().setId( getFormGroupId( field ) );

        FormLabel label = new FormLabel();
        label.setText( field.getLabel() );

        Widget input = getInputWidget().asWidget();

        label.setFor( input.getElement().getId() );
        group.add( label );
        group.add( input );

        HelpBlock helpBlock = new HelpBlock();
        helpBlock.setId( getHelpBlokId( field ) );

        group.add( helpBlock );
        return group;
    }

    public F getField() {
        return field;
    }

    public abstract String getName();

    public abstract void initInputWidget();

    public abstract IsWidget getInputWidget();

    public abstract String getSupportedFieldDefinitionCode();

    protected String getFormGroupId( F field ) {
        return generateRelatedId( field, FormViewValidator.FORM_GROUP_SUFFIX );
    }

    protected String getHelpBlokId( F field ) {
        return generateRelatedId( field, FormViewValidator.HELP_BLOCK_SUFFIX );
    }

    private String generateRelatedId( F field, String suffix ) {
        if ( field == null ) {
            return "";
        }
        return field.getName() + suffix;
    }
}
