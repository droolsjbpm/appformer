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

package org.uberfire.java.nio.fs.jgit.util.commands;

import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.util.exceptions.GitException;

public class BranchUtil {

    public static void deleteUnfilteredBranches(final Repository repository,
                                                final List<String> branchesToKeep) throws GitAPIException {
        if (branchesToKeep == null || branchesToKeep.isEmpty()) {
            return;
        }

        final org.eclipse.jgit.api.Git git = org.eclipse.jgit.api.Git.wrap(repository);
        final String[] toDelete = git.branchList()
                .call()
                .stream()
                .map(Ref::getName)
                .map(fullname -> fullname.substring(fullname.lastIndexOf('/') + 1))
                .filter(name -> !branchesToKeep.contains(name))
                .toArray(String[]::new);
        git.branchDelete()
                .setBranchNames(toDelete)
                .setForce(true)
                .call();
    }

    public static void existsBranch(final Git git,
                                    final String branch) {
        if (git.getRef(branch) == null) {
            throw new GitException(String.format("Branch <<%s>> does not exists",
                                                 branch));
        }
    }

    public static RevCommit getCommonAncestor(final Git git,
                                              final String branchA,
                                              final String branchB) {

        try (final RevWalk revWalk = new RevWalk(git.getRepository())) {
            final RevCommit lastSourceCommit = git.getLastCommit(branchA);
            final RevCommit lastTargetCommit = git.getLastCommit(branchB);

            final RevCommit commitA = revWalk.lookupCommit(lastSourceCommit);
            final RevCommit commitB = revWalk.lookupCommit(lastTargetCommit);

            revWalk.setRevFilter(RevFilter.MERGE_BASE);
            revWalk.markStart(commitA);
            revWalk.markStart(commitB);
            return revWalk.next();
        } catch (Exception e) {
            throw new GitException("Problem when trying to get common ancestor",
                                   e);
        }
    }
}
