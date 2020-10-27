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

package org.dashbuilder.displayer.client.component.function;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.dashbuilder.displayer.external.ExternalComponentFunction;
import org.dashbuilder.external.service.BackendComponentFunctionService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.EntryPoint;

/**
 * Generated wrapped client functions to proxy the call using BackendFunctionLoaderService.
 *
 */
@EntryPoint
public class BackendFunctionLoader {

    private static final String UNKNOW_BACKEND_ERROR = "Unknow backend error.";

    @Inject
    ComponentFunctionLocator componentFunctionLocator;

    @Inject
    Caller<BackendComponentFunctionService> backendFunctionLoaderService;

    @PostConstruct
    public void loadBackendFunctions() {
        backendFunctionLoaderService.call((List<String> result) -> this.registerFunctions(result)).listFunctions();
    }

    private void registerFunctions(List<String> result) {
        result.forEach(name -> componentFunctionLocator.registerFunction(new ExternalComponentFunction() {

            @Override
            public String getName() {
                return name;
            }

            @Override
            public void exec(Map<String, Object> params, Consumer<Object> onFinish, Consumer<String> onError) {
                backendFunctionLoaderService.call(onFinish::accept, (Object message, Throwable throwable) -> {
                    String errorMessage = UNKNOW_BACKEND_ERROR;
                    if (throwable != null && throwable.getMessage() != null) {
                        errorMessage = throwable.getMessage();
                    } else if (message != null) {
                        errorMessage = message.toString();
                    }
                    onError.accept(errorMessage);
                    return false;
                }).callFunction(name, params);
            }
        }));

    }

}