/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.appformer.kogito.bridge.client.stateControl.interop;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import org.appformer.kogito.bridge.client.stateControl.registry.interop.KogitoJSCommandRegistry;

/**
 * Represents the TypeScript StateControl engine present on the Envelope
 */
@JsType(isNative = true, namespace = "window", name = "envelope")
public class StateControl {

    /**
     * Retrieves the {@link KogitoJSCommandRegistry}
     *
     * @param <C> Anything that can be considered a command
     * @return The {@link KogitoJSCommandRegistry}
     */
    public native <C> KogitoJSCommandRegistry<C> getCommandRegistry();

    /**
     * Sets the {@link StateControlCommand} that will be called when the StateControl engine is notified to undo last
     * command on the {@link KogitoJSCommandRegistry}
     *
     * @param command The command to execute on undo
     */
    @JsMethod
    public native void setUndoCommand(StateControlCommand command);

    /**
     * Sets the {@link StateControlCommand} that will be called when the StateControl engine is notified to redo last
     * undone command on the {@link KogitoJSCommandRegistry}
     *
     * @param command The command to execute on redo
     */
    @JsMethod
    public native void setRedoCommand(StateControlCommand command);

    @JsProperty(name = "stateControl")
    public static native StateControl get();
}
