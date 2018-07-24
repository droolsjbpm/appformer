/*
* Copyright 2018 Red Hat, Inc. and/or its affiliates.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.guvnor.rest.backend;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;

import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.rest.client.CloneProjectRequest;
import org.guvnor.rest.client.CreateProjectRequest;
import org.guvnor.rest.client.JobRequest;
import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.JobStatus;
import org.guvnor.rest.client.Space;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.spaces.SpacesAPI;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ProjectResourceJobTest {

    @Mock
    private SpacesAPI spacesAPI;

    @Mock
    private JobResultManager jobManager;

    @Mock
    private JobRequestScheduler jobRequestObserver;

    @Mock
    private OrganizationalUnitService organizationalUnitService;

    @Mock
    private WorkspaceProjectService workspaceProjectService;

    @Captor
    private ArgumentCaptor<JobResult> jobResultArgumentCaptor;

    @InjectMocks
    ProjectResource projectResource = new ProjectResource() {
        protected Variant getDefaultVariant() {
            return null;
        }

        protected void assertObjectExists(final Object o,
                                          final String objectInfo,
                                          final String objectName) {

        }

        protected Response createAcceptedStatusResponse(final JobRequest jobRequest) {
            return null;
        }
    };

    @Test
    public void cloneProject() throws Exception {

        projectResource.cloneProject("spaceName",
                                     new CloneProjectRequest());

        verify(jobManager).putJob(jobResultArgumentCaptor.capture());
        assertEquals(JobStatus.ACCEPTED, jobResultArgumentCaptor.getValue().getStatus());
    }

    @Test
    public void createProject() throws Exception {

        projectResource.createProject("spaceName",
                                      new CreateProjectRequest());

        verify(jobManager).putJob(jobResultArgumentCaptor.capture());
        assertEquals(JobStatus.ACCEPTED, jobResultArgumentCaptor.getValue().getStatus());
    }

    @Test
    public void deleteProject() throws Exception {

        projectResource.deleteProject("spaceName",
                                      "projectName");

        verify(jobManager).putJob(jobResultArgumentCaptor.capture());
        assertEquals(JobStatus.ACCEPTED, jobResultArgumentCaptor.getValue().getStatus());
    }

    @Test
    public void compileProject() throws Exception {

        projectResource.compileProject("spaceName",
                                       "projectName");

        verify(jobManager).putJob(jobResultArgumentCaptor.capture());
        assertEquals(JobStatus.ACCEPTED, jobResultArgumentCaptor.getValue().getStatus());
    }

    @Test
    public void installProject() throws Exception {

        projectResource.installProject("spaceName",
                                       "projectName");

        verify(jobManager).putJob(jobResultArgumentCaptor.capture());
        assertEquals(JobStatus.ACCEPTED, jobResultArgumentCaptor.getValue().getStatus());
    }

    @Test
    public void testProject() throws Exception {

        projectResource.testProject("spaceName",
                                    "projectName");

        verify(jobManager).putJob(jobResultArgumentCaptor.capture());
        assertEquals(JobStatus.ACCEPTED, jobResultArgumentCaptor.getValue().getStatus());
    }

    @Test
    public void deployProject() throws Exception {

        projectResource.deployProject("spaceName",
                                      "projectName");

        verify(jobManager).putJob(jobResultArgumentCaptor.capture());
        assertEquals(JobStatus.ACCEPTED, jobResultArgumentCaptor.getValue().getStatus());
    }

    @Test
    public void createSpace() throws Exception {

        projectResource.createSpace(new Space());

        verify(jobManager).putJob(jobResultArgumentCaptor.capture());
        assertEquals(JobStatus.ACCEPTED, jobResultArgumentCaptor.getValue().getStatus());
    }

    @Test
    public void deleteSpace() throws Exception {

        projectResource.deleteSpace("spaceName");

        verify(jobManager).putJob(jobResultArgumentCaptor.capture());
        assertEquals(JobStatus.ACCEPTED, jobResultArgumentCaptor.getValue().getStatus());
    }
}