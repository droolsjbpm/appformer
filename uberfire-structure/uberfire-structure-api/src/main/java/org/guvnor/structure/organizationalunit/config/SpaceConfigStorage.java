/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.organizationalunit.config;

import java.util.List;

import org.guvnor.structure.repositories.changerequest.ChangeRequest;

public interface SpaceConfigStorage {

    void setup(final String spaceName);

    SpaceInfo loadSpaceInfo();

    void saveSpaceInfo(final SpaceInfo spaceInfo);

    BranchPermissions loadBranchPermissions(final String branchName,
                                            final String projectIdentifier);

    void saveBranchPermissions(final String branchName,
                               final String projectIdentifier,
                               final BranchPermissions branchPermissions);

    void deleteBranchPermissions(final String branchName,
                                 final String projectIdentifier);

    boolean isInitialized();

    void startBatch();

    void endBatch();

    void close();

    List<ChangeRequest> loadChangeRequests(final String repositoryAlias);

    ChangeRequest loadChangeRequest(final String repositoryAlias,
                                    final Long changeRequestId);

    void saveChangeRequest(final String repositoryAlias,
                           final ChangeRequest changeRequest);

    void deleteAllChangeRequests(final String repositoryAlias);

    void deleteChangeRequest(final String repositoryAlias,
                             final Long changeRequestId);

    void deleteRepository(final String repositoryAlias);

    List<Long> getChangeRequestIds(final String repositoryAlias);
}
