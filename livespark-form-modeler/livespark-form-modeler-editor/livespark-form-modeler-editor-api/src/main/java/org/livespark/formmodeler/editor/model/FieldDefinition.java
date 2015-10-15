/*
 * Copyright 2015 JBoss Inc
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

package org.livespark.formmodeler.editor.model;

import org.livespark.formmodeler.editor.model.util.IDGenerator;
import org.livespark.formmodeler.editor.service.FieldManager;

public abstract class FieldDefinition {

    public static final String ID_PREFFIX = "field" + FieldManager.FIELD_NAME_SEPARATOR;

    protected boolean annotatedId;

    private String id;

    private String code = getCode();

    protected String name;

    protected String label;

    protected Boolean required = Boolean.FALSE;

    protected Boolean readonly = Boolean.FALSE;

    protected String modelName;

    protected String boundPropertyName;

    protected String standaloneClassName;

    public FieldDefinition() {
        id = ID_PREFFIX + IDGenerator.generateRandomId();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel( String label ) {
        this.label = label;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired( Boolean required ) {
        this.required = required;
    }

    public Boolean getReadonly() {
        return readonly;
    }

    public void setReadonly( Boolean readonly ) {
        this.readonly = readonly;
    }

    public String getBindingExpression() {
        String bindingExpression = modelName;

        if ( boundPropertyName!= null && !boundPropertyName.isEmpty() ) bindingExpression += "." + boundPropertyName;

        return bindingExpression;
    }

    public boolean isAnnotatedId() {
        return annotatedId;
    }

    public void setAnnotatedId( boolean annotatedId ) {
        this.annotatedId = annotatedId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName( String modelName ) {
        this.modelName = modelName;
    }

    public String getBoundPropertyName() {
        return boundPropertyName;
    }

    public void setBoundPropertyName( String boundPropertyName ) {
        this.boundPropertyName = boundPropertyName;
    }

    public String getStandaloneClassName() {
        return standaloneClassName;
    }

    public void setStandaloneClassName(String standaloneClassName) {
        this.standaloneClassName = standaloneClassName;
    }

    public void copyFrom( FieldDefinition other ) {
        if ( other == null ) return;
        setLabel(other.getLabel());
        if ( !other.isAnnotatedId() ) setReadonly( other.getReadonly() );
        setRequired( other.getRequired() );
        doCopyFrom( other );
    }

    public abstract String getCode();

    public abstract String[] getSupportedTypes();

    protected abstract void doCopyFrom(FieldDefinition other);
}
