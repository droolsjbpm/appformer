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

package org.uberfire.java.nio.fs.jgit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.AbstractBasicFileAttributeView;
import org.uberfire.java.nio.base.FileTimeImpl;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.attribute.BasicFileAttributeView;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.file.attribute.FileTime;
import org.uberfire.java.nio.fs.jgit.util.model.CommitHistory;
import org.uberfire.java.nio.fs.jgit.util.model.PathInfo;
import org.uberfire.java.nio.fs.jgit.util.model.PathType;

/**
 *
 */
public class JGitBasicAttributeView extends AbstractBasicFileAttributeView<JGitPathImpl> {

    private BasicFileAttributes attrs = null;

    public JGitBasicAttributeView(final JGitPathImpl path) {
        super(path);
    }

    @Override
    public BasicFileAttributes readAttributes() throws IOException {
        if (attrs == null) {
            attrs = buildAttrs(path.getFileSystem(),
                               path.getRefTree(),
                               path.getPath());
        }
        return attrs;
    }

    @Override
    public Class<? extends BasicFileAttributeView>[] viewTypes() {
        return new Class[]{BasicFileAttributeView.class, JGitBasicAttributeView.class};
    }

    private BasicFileAttributes buildAttrs(final JGitFileSystem fs,
                                           final String branchName,
                                           final String path) {
        final PathInfo pathInfo = fs.getGit().getPathInfo(branchName,
                                                          path);

        if (pathInfo == null || pathInfo.getPathType().equals(PathType.NOT_FOUND)) {
            throw new NoSuchFileException(path);
        }

        final Ref ref = fs.getGit().getRef(branchName);

        return new BasicFileAttributes() {

            private FileTime lastModifiedDate = null;
            private FileTime creationDate = null;

            @Override
            public FileTime lastModifiedTime() {
                if (lastModifiedDate == null) {
                    try {
                        lastModifiedDate = new FileTimeImpl(fs.getGit().getLastCommit(ref).getCommitterIdent().getWhen().getTime());
                    } catch (final Exception e) {
                        lastModifiedDate = new FileTimeImpl(0);
                    }
                }
                return lastModifiedDate;
            }

            @Override
            public FileTime lastAccessTime() {
                return lastModifiedTime();
            }

            @Override
            public FileTime creationTime() {
                if (creationDate == null) {
                    try {
                        creationDate = new FileTimeImpl(fs.getGit().getFirstCommit(ref).getCommitterIdent().getWhen().getTime());
                    } catch (final Exception e) {
                        creationDate = new FileTimeImpl(0);
                    }
                }
                return creationDate;
            }

            @Override
            public boolean isRegularFile() {
                return pathInfo.getPathType().equals(PathType.FILE);
            }

            @Override
            public boolean isDirectory() {
                return pathInfo.getPathType().equals(PathType.DIRECTORY);
            }

            @Override
            public boolean isSymbolicLink() {
                return false;
            }

            @Override
            public boolean isOther() {
                return false;
            }

            @Override
            public long size() {
                return pathInfo.getSize();
            }

            @Override
            public Object fileKey() {
                return pathInfo.getObjectId() == null ? null : pathInfo.getObjectId().toString();
            }
        };
    }
}
