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

package org.livespark.formmodeler.editor.service;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.livespark.formmodeler.model.FormDefinition;
import org.livespark.formmodeler.renderer.service.FormRenderingContext;
import org.uberfire.backend.vfs.Path;

@Portable
public class FormEditorFormRenderingContext extends FormRenderingContext {
    private Path formPath;

    public FormEditorFormRenderingContext( @MapsTo( "rootForm" ) FormDefinition rootForm,
                                           @MapsTo( "model" ) Object model,
                                           @MapsTo( "formPath" ) Path formPath ) {
        super( rootForm, model );
        this.formPath = formPath;
    }

    public void setFormPath( Path formPath ) {
        this.formPath = formPath;
    }

    public Path getFormPath() {
        return formPath;
    }

    @Override
    protected FormRenderingContext getNewInstance( FormDefinition form, Object model ) {
        return new FormEditorFormRenderingContext( form, model, formPath );
    }
}
