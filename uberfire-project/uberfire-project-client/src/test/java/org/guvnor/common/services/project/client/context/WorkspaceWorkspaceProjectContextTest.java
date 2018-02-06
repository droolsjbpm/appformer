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

package org.guvnor.common.services.project.client.context;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.guvnor.common.services.project.context.ProjectContextChangeHandle;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeHandler;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.EventSourceMock;

@RunWith(MockitoJUnitRunner.class)
public class WorkspaceWorkspaceProjectContextTest {

    @Spy
    private EventSourceMock<WorkspaceProjectContextChangeEvent> changeEvent = new EventSourceMock<>();

    private WorkspaceProjectContext context;

    @Before
    public void setUp() throws Exception {
        context = new WorkspaceProjectContext(changeEvent);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                context.onProjectContextChanged((WorkspaceProjectContextChangeEvent) invocationOnMock.getArguments()[0]);
                return null;
            }
        }).when(changeEvent).fire(any(WorkspaceProjectContextChangeEvent.class));
    }

    @Test
    public void testGetActiveRepositoryRoot() throws Exception {

        final Path devRoot = mock(Path.class);

        context.setActiveWorkspaceProject(new WorkspaceProject(mock(OrganizationalUnit.class),
                                                               mock(Repository.class),
                                                               new Branch("dev",
                                                                          devRoot),
                                                               mock(Module.class)));

        assertEquals(Optional.of(devRoot),
                     context.getActiveRepositoryRoot());
    }

    @Test
    public void testContextChanged() throws Exception {
        final OrganizationalUnit oldOrganizationalUnit = mock(OrganizationalUnit.class);
        final Repository oldRepository = mock(Repository.class);
        final Package oldPackage = new Package();
        final Module oldModule = new Module();

        context.setActiveOrganizationalUnit(oldOrganizationalUnit);
        WorkspaceProject oldProject = new WorkspaceProject(oldOrganizationalUnit,
                                                           oldRepository,
                                                           mock(Branch.class),
                                                           mock(Module.class));
        context.setActiveWorkspaceProject(oldProject);
        context.setActivePackage(oldPackage);
        context.setActiveModule(oldModule);

        final OrganizationalUnit newOrganizationalUnit = mock(OrganizationalUnit.class);
        final Branch newBranch = new Branch("master",
                                            mock(Path.class));
        final Package newPackage = new Package();
        final Module newModule = new Module();

        final WorkspaceProjectContextChangeHandler changeHandler = mock(WorkspaceProjectContextChangeHandler.class);
        context.addChangeHandler(changeHandler);

        final WorkspaceProject newWorkspaceProject = new WorkspaceProject(newOrganizationalUnit,
                                                                          mock(Repository.class),
                                                                          newBranch,
                                                                          mock(Module.class));
        WorkspaceProjectContextChangeEvent event = new WorkspaceProjectContextChangeEvent(newWorkspaceProject,
                                                                               newModule,
                                                                               newPackage);
        context.onProjectContextChanged(event);

        assertEquals(Optional.of(newOrganizationalUnit),
                     context.getActiveOrganizationalUnit());
        assertEquals(Optional.of(newWorkspaceProject),
                     context.getActiveWorkspaceProject());
        assertEquals(Optional.of(newModule),
                     context.getActiveModule());
        assertEquals(Optional.of(newPackage),
                     context.getActivePackage());
        verify(changeHandler).onChange(eq(new WorkspaceProjectContextChangeEvent(oldProject, oldModule, oldPackage)), eq(event));
    }

    @Test
    public void testContextChangeHandlerGetsRemoved() throws Exception {
        WorkspaceProjectContextChangeHandler changeHandler = mock(WorkspaceProjectContextChangeHandler.class);
        ProjectContextChangeHandle handle = context.addChangeHandler(changeHandler);

        context.onProjectContextChanged(new WorkspaceProjectContextChangeEvent());

        verify(changeHandler).onChange(any(), eq(new WorkspaceProjectContextChangeEvent()));

        context.removeChangeHandler(handle);

        reset(changeHandler);

        context.onProjectContextChanged(new WorkspaceProjectContextChangeEvent());

        verify(changeHandler,
               never()).onChange(any(), any());
    }
}
