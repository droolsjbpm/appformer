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

package org.uberfire.java.nio.fs.jgit.util.commands;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.internal.ketch.KetchLeaderCache;
import org.eclipse.jgit.internal.storage.file.WindowCache;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.WindowCacheConfig;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.util.FileUtils;
import org.uberfire.commons.data.Pair;
import org.uberfire.java.nio.fs.jgit.util.Git;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class Clone {

    private final File repoDir;
    private final String origin;
    private final CredentialsProvider credentialsProvider;
    private final boolean isMirror;
    private final KetchLeaderCache leaders;

    public Clone(final File directory,
                 final String origin,
                 final boolean isMirror,
                 final CredentialsProvider credentialsProvider,
                 final KetchLeaderCache leaders) {
        this.repoDir = checkNotNull("directory",
                                    directory);
        this.origin = checkNotEmpty("origin",
                                    origin);
        this.isMirror = isMirror;
        this.credentialsProvider = credentialsProvider;
        this.leaders = leaders;
    }

    public Optional<Git> execute() throws InvalidRemoteException {
        final Git git = Git.createRepository(repoDir,
                                             null);

        if (git != null) {
            try {

                final Collection<RefSpec> refSpecList;
                if (isMirror) {
                    refSpecList = singletonList(new RefSpec("+refs/*:refs/*"));
                } else {
                    refSpecList = emptyList();
                }
                final Pair<String, String> remote = Pair.newPair("origin",
                                                                 origin);
                git.fetch(credentialsProvider,
                          remote,
                          refSpecList);

                if (isMirror) {
                    final StoredConfig config = git.getRepository().getConfig();
                    config.setBoolean("remote",
                                      "origin",
                                      "mirror",
                                      true);
                    try {
                        config.save();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                git.syncRemote(remote);

                if (git.isKetchEnabled()) {
                    git.convertRefTree();
                    git.updateLeaders(leaders);
                }

                git.setHeadAsInitialized();

                return Optional.of(git);
            } catch (Exception e) {
                cleanupDir(git.getRepository().getDirectory());
                throw new CloneException();
            }
        }

        return Optional.empty();
    }

    private void cleanupDir(final File gitDir) {

        try {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                //this operation forces a cache clean freeing any lock -> windows only issue!
                WindowCache.reconfigure(new WindowCacheConfig());
            }
            FileUtils.delete(gitDir,
                             FileUtils.RECURSIVE | FileUtils.RETRY);
        } catch (java.io.IOException e) {
            throw new org.uberfire.java.nio.IOException("Failed to remove the git repository.",
                                                        e);
        }
    }

    public class CloneException extends RuntimeException {

    }
}
