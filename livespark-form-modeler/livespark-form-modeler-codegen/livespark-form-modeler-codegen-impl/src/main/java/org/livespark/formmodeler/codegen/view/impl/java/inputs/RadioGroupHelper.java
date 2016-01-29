/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.livespark.formmodeler.codegen.view.impl.java.inputs;

import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.jboss.forge.roaster.model.source.PropertySource;
import org.livespark.formmodeler.codegen.SourceGenerationContext;
import org.livespark.formmodeler.codegen.view.impl.java.RequiresCustomCode;
import org.livespark.formmodeler.codegen.view.impl.java.RequiresExtraFields;
import org.livespark.formmodeler.model.impl.basic.selectors.RadioGroupFieldDefinition;
import org.livespark.formmodeler.model.impl.basic.selectors.SelectorOption;

import static org.livespark.formmodeler.codegen.util.SourceGenerationUtil.*;

public class RadioGroupHelper extends AbstractInputCreatorHelper<RadioGroupFieldDefinition> implements RequiresCustomCode<RadioGroupFieldDefinition>, RequiresExtraFields<RadioGroupFieldDefinition> {

    public static final String SET_RADIO_DEFAULT_VALUE_METHOD_NAME = "setRadioDefaultValue_";

    public static final String RADIO_GROUP_NAME_SUFFIX = "_RadioGroupName";
    public static final String NESTED_RADIO_SUFFIX = "_Radio_";

    public static final String RADIO_NAME = "Radio";
    public static final String RADIO_CLASSNAME = "org.gwtbootstrap3.client.ui." + RADIO_NAME;

    public static final String INLINE_RADIO_NAME = "InlineRadio";
    public static final String INLINE_RADIO_CLASSNAME = "org.gwtbootstrap3.client.ui." + INLINE_RADIO_NAME;

    @Override
    public boolean isInputInjectable() {
        return false;
    }

    @Override
    public String getSupportedFieldTypeCode() {
        return RadioGroupFieldDefinition._CODE;
    }

    @Override
    public String getInputWidget( RadioGroupFieldDefinition fieldDefinition ) {
        return "org.gwtbootstrap3.client.ui.StringRadioGroup";
    }

    @Override
    public String getInputInitLiteral( SourceGenerationContext context, RadioGroupFieldDefinition fieldDefinition ) {
        return "new StringRadioGroup( " + fieldDefinition.getName() + RADIO_GROUP_NAME_SUFFIX + " );";
    }

    @Override
    public void addCustomCode( RadioGroupFieldDefinition fieldDefinition, SourceGenerationContext context, JavaClassSource viewClass ) {

        MethodSource<JavaClassSource> initFormMethod = viewClass.getMethod( INIT_FORM_METHOD );
        StringBuffer body = new StringBuffer( initFormMethod.getBody() );

        String defaultValue = null;

        for (int i = 0; i<fieldDefinition.getOptions().size(); i++) {

            String inputName = fieldDefinition.getName() + NESTED_RADIO_SUFFIX + i;

            SelectorOption option = fieldDefinition.getOptions().get( i );

            body.append( inputName )
                    .append( ".setFormValue( \"" )
                    .append( option.getValue() )
                    .append( "\");" );

            body.append( fieldDefinition.getName() )
                    .append( ".add(" )
                    .append( inputName )
                    .append( ");" );

            if ( option.getDefaultValue() ) {
                defaultValue = option.getValue();
            }

        }

        initFormMethod.setBody( body.toString() );

        if ( defaultValue != null ) {
            body = new StringBuffer( );
            body.append( "if (" ).append( IS_NEW_MODEL_METHOD_CALL).append( ") {" );
            body.append( fieldDefinition.getName() )
                    .append( ".setValue( \"" )
                    .append( defaultValue )
                    .append( "\", true );" );
            body.append( "}" );

            String setDefaultValueMethod = SET_RADIO_DEFAULT_VALUE_METHOD_NAME + fieldDefinition.getName();

            viewClass.addMethod()
                    .setName( setDefaultValueMethod )
                    .setReturnTypeVoid()
                    .setProtected()
                    .setBody( body.toString() );

            MethodSource<JavaClassSource> beforeDisplayMethod = viewClass.getMethod( BEFORE_DISPLAY_METHOD, void.class );
            body = new StringBuffer( beforeDisplayMethod.getBody() == null ? "" : beforeDisplayMethod.getBody() );
            body.append( setDefaultValueMethod ).append( "();" );
            beforeDisplayMethod.setBody( body.toString() );
        }
    }

    @Override
    public String getReadonlyMethod( String fieldName, String readonlyParam ) {
        return ""; // No default readonly required
    }

    @Override
    public String getExtraReadOnlyCode( RadioGroupFieldDefinition fieldDefinition, String readonlyParam ) {
        StringBuffer readonlySrc = new StringBuffer( );

        for (int i = 0; i<fieldDefinition.getOptions().size(); i++) {
            readonlySrc.append( fieldDefinition.getName() )
                    .append( NESTED_RADIO_SUFFIX )
                    .append( i )
                    .append( ".setEnabled( !" )
                    .append( readonlyParam )
                    .append( ");" );
        }

        return readonlySrc.toString();
    }

    @Override
    public void addExtraFields( JavaClassSource viewClass, RadioGroupFieldDefinition fieldDefinition ) {
        String inputClassName;
        String inputFullClassName;

        if ( fieldDefinition.getInline() ) {
            inputClassName = INLINE_RADIO_NAME;
            inputFullClassName = INLINE_RADIO_CLASSNAME;
        } else {
            inputClassName = RADIO_NAME;
            inputFullClassName = RADIO_CLASSNAME;
        }

        viewClass.addImport( GWT_DOM_CLASSNAME );
        viewClass.addImport( inputFullClassName );

        String groupName = fieldDefinition.getName() + RADIO_GROUP_NAME_SUFFIX;

        addProperty( viewClass, "String", groupName, "DOM.createUniqueId()" );

        for (int i = 0; i<fieldDefinition.getOptions().size(); i++) {

            String inputName = fieldDefinition.getName() + NESTED_RADIO_SUFFIX + i;

            SelectorOption option = fieldDefinition.getOptions().get( i );

            String text = option.getText();

            if ( text == null || text.isEmpty()) {
                viewClass.addImport( GWT_SAFE_HTML_UTILS_CLASSNAME );
                text = GWT_SAFE_HTML_UTILS_FROM_TRUSTED_SOURCE;
            } else {
                text = "\"" + text + "\"";
            }

            String initializer = "new " + inputClassName + "(" + groupName + ", " + text + ");";

            addProperty( viewClass, inputClassName, inputName, initializer );
        }
    }

    protected void addProperty( JavaClassSource viewClass, String type, String name, String initializer ) {
        PropertySource<JavaClassSource> property = viewClass.addProperty( type, name );

        FieldSource<JavaClassSource> field = property.getField();
        field.setPrivate();
        field.setLiteralInitializer( initializer );

        property.removeAccessor();
        property.removeMutator();
    }

}
