/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.appformer.kogito.bridge.client.keyboardshortcuts;

import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * Javascript bridge to access actual KeyboardShortcutsApi available in the envelope namespace
 */
@JsType(isNative = true, namespace = "window", name = "envelope")
public class KeyboardShortcutsApiInteropWrapper {

    public native int registerKeyPress(final String combination,
                                       final String label,
                                       final KeyboardShortcutsApi.Action onKeyDown,
                                       final KeyboardShortcutsApi.Opts opts);

    public native int registerKeyDownThenUp(final String combination,
                                            final String label,
                                            final KeyboardShortcutsApi.Action onKeyDown,
                                            final KeyboardShortcutsApi.Action onKeyUp,
                                            final KeyboardShortcutsApi.Opts opts);

    public native void deregister(final int id);

    @JsProperty(name = "keyboardShortcuts")
    public static native KeyboardShortcutsApiInteropWrapper get();
}
