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

package org.uberfire.java.nio.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.data.Index.atIndex;
import static org.uberfire.java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static org.uberfire.java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileUtilityMethodsTest extends AbstractBaseTest {

    @Test
    public void newBufferedReader() throws IOException {
        final Path dir = newTempDir();

        final OutputStream out = Files.newOutputStream(dir.resolve("file.txt"));
        assertThat(out).isNotNull();

        out.write("content".getBytes());
        out.close();

        final BufferedReader reader = Files.newBufferedReader(dir.resolve("file.txt"),
                                                              Charset.defaultCharset());
        assertThat(reader).isNotNull();
        assertThat(reader.readLine()).isNotNull().isEqualTo("content");
        assertThat(reader.readLine()).isNull();
        reader.close();
        try {
            reader.read();
            fail("can't read closed stream");
        } catch (Exception ex) {
        }
    }

    @Test(expected = NoSuchFileException.class)
    public void newBufferedReaderNoSuchFileException() {
        Files.newBufferedReader(Paths.get("/some/file/here"),
                                Charset.defaultCharset());
    }

    @Test(expected = NoSuchFileException.class)
    public void newBufferedReaderNoSuchFileException2() {
        Files.newBufferedReader(newTempDir(),
                                Charset.defaultCharset());
    }

    @Test(expected = IllegalArgumentException.class)
    public void newBufferedReaderNull1() {
        Files.newBufferedReader(null,
                                Charset.defaultCharset());
    }

    @Test(expected = IllegalArgumentException.class)
    public void newBufferedReaderNull2() {
        Files.newBufferedReader(Files.createTempFile("foo",
                                                     "bar"),
                                null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newBufferedReaderNull3() {
        Files.newBufferedReader(null,
                                null);
    }

    @Test
    public void newBufferedWriter() throws IOException {
        final Path dir = newTempDir();
        final BufferedWriter writer = Files.newBufferedWriter(dir.resolve("myfile.txt"),
                                                              Charset.defaultCharset());
        assertThat(writer).isNotNull();
        writer.write("content");
        writer.close();

        final BufferedReader reader = Files.newBufferedReader(dir.resolve("myfile.txt"),
                                                              Charset.defaultCharset());
        assertThat(reader).isNotNull();
        assertThat(reader.readLine()).isNotNull().isEqualTo("content");
        assertThat(reader.readLine()).isNull();
        reader.close();

        Files.newBufferedWriter(Files.createTempFile(null,
                                                     null),
                                Charset.defaultCharset());
    }

    @Test(expected = IllegalArgumentException.class)
    public void newBufferedWriterNull1() {
        Files.newBufferedWriter(null,
                                Charset.defaultCharset());
    }

    @Test(expected = IllegalArgumentException.class)
    public void newBufferedWriterNull2() {
        Files.newBufferedWriter(newTempDir().resolve("some"),
                                null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newBufferedWriterNull3() {
        Files.newBufferedWriter(null,
                                null);
    }

    @Test
    public void copyIn2Path() throws IOException {
        final Path dir = newTempDir();
        final BufferedWriter writer = Files.newBufferedWriter(dir.resolve("myfile.txt"),
                                                              Charset.defaultCharset());
        assertThat(writer).isNotNull();
        writer.write("content");
        writer.close();

        final InputStream is = Files.newInputStream(dir.resolve("myfile.txt"));
        Files.copy(is,
                   dir.resolve("my_new_file.txt"));
        is.close();

        final BufferedReader reader = Files.newBufferedReader(dir.resolve("my_new_file.txt"),
                                                              Charset.defaultCharset());
        assertThat(reader).isNotNull();
        assertThat(reader.readLine()).isNotNull().isEqualTo("content");
        assertThat(reader.readLine()).isNull();
        reader.close();
    }

    @Test
    public void copyIn2PathReplaceExisting() throws IOException {
        final Path dir = newTempDir();
        final BufferedWriter writer = Files.newBufferedWriter(dir.resolve("myfile.txt"),
                                                              Charset.defaultCharset());
        assertThat(writer).isNotNull();
        writer.write("content");
        writer.close();

        final BufferedWriter writer2 = Files.newBufferedWriter(dir.resolve("my_new_file.txt"),
                                                               Charset.defaultCharset());
        assertThat(writer2).isNotNull();
        writer2.write("empty_content");
        writer2.close();

        final InputStream is = Files.newInputStream(dir.resolve("myfile.txt"));
        Files.copy(is,
                   dir.resolve("my_new_file.txt"),
                   REPLACE_EXISTING);
        is.close();

        final BufferedReader reader = Files.newBufferedReader(dir.resolve("my_new_file.txt"),
                                                              Charset.defaultCharset());
        assertThat(reader).isNotNull();
        assertThat(reader.readLine()).isNotNull().isEqualTo("content");
        assertThat(reader.readLine()).isNull();
        reader.close();
    }

    @Test
    public void copyIn2PathReplaceExistingNotExists() throws IOException {
        final Path dir = newTempDir();
        final BufferedWriter writer = Files.newBufferedWriter(dir.resolve("myfile.txt"),
                                                              Charset.defaultCharset());
        assertThat(writer).isNotNull();
        writer.write("content");
        writer.close();

        final InputStream is = Files.newInputStream(dir.resolve("myfile.txt"));
        Files.copy(is,
                   dir.resolve("my_new_file.txt"),
                   REPLACE_EXISTING);
        is.close();

        final BufferedReader reader = Files.newBufferedReader(dir.resolve("my_new_file.txt"),
                                                              Charset.defaultCharset());
        assertThat(reader).isNotNull();
        assertThat(reader.readLine()).isNotNull().isEqualTo("content");
        assertThat(reader.readLine()).isNull();
        reader.close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void copyIn2PathNull1() {
        Files.copy((InputStream) null,
                   newTempDir().resolve("my_new_file.txt"),
                   REPLACE_EXISTING);
    }

    @Test(expected = IllegalArgumentException.class)
    public void copyIn2PathNull2() {
        Files.copy(Files.newInputStream(Files.createTempFile("foo",
                                                             "bar")),
                   null,
                   REPLACE_EXISTING);
    }

    @Test(expected = IllegalArgumentException.class)
    public void copyIn2PathNull3() {
        Files.copy(Files.newInputStream(Files.createTempFile("foo",
                                                             "bar")),
                   newTempDir().resolve("my_new_file.txt"),
                   null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void copyIn2PathNull4() {
        Files.copy(Files.newInputStream(Files.createTempFile("foo",
                                                             "bar")),
                   newTempDir().resolve("my_new_file.txt"),
                   new CopyOption[]{null});
    }

    @Test(expected = UnsupportedOperationException.class)
    public void copyIn2PathInvalidOption() {
        Files.copy(Files.newInputStream(Files.createTempFile("foo",
                                                             "bar")),
                   newTempDir().resolve("my_new_file.txt"),
                   NOFOLLOW_LINKS);
    }

    @Test
    public void copyPath2Out() throws IOException {
        final Path dir = newTempDir();
        final BufferedWriter writer = Files.newBufferedWriter(dir.resolve("myfile.txt"),
                                                              Charset.defaultCharset());
        assertThat(writer).isNotNull();
        writer.write("content");
        writer.close();

        final OutputStream os = Files.newOutputStream(dir.resolve("my_new_file.txt"));
        Files.copy(dir.resolve("myfile.txt"),
                   os);
        os.close();

        final BufferedReader reader = Files.newBufferedReader(dir.resolve("my_new_file.txt"),
                                                              Charset.defaultCharset());
        assertThat(reader).isNotNull();
        assertThat(reader.readLine()).isNotNull().isEqualTo("content");
        assertThat(reader.readLine()).isNull();
        reader.close();
    }

    @Test(expected = NoSuchFileException.class)
    public void copyPath2OutNotExists() throws IOException {
        try (OutputStream os = Files.newOutputStream(newTempDir().resolve("my_new_file.txt"))) {
            Files.copy(newTempDir().resolve("myfile.txt"),
                       os);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void copyPath2OutNull1() throws IOException {
        try (OutputStream os = Files.newOutputStream(newTempDir().resolve("my_new_file.txt"))) {
            Files.copy(null,
                       os);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void copyPath2OutNull2() {
        Files.copy(Files.createTempFile("foo",
                                        "bar"),
                   null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void copyPath2OutInvalidOption() {
        Files.copy(null,
                   null);
    }

    @Test
    public void readAllBytes() throws IOException {
        final Path dir = newTempDir();
        final BufferedWriter writer = Files.newBufferedWriter(dir.resolve("myfile.txt"),
                                                              Charset.defaultCharset());
        assertThat(writer).isNotNull();
        writer.write("content");
        writer.close();

        final byte[] result = Files.readAllBytes(dir.resolve("myfile.txt"));

        assertThat(result).isNotEmpty().hasSize("content".getBytes().length).isEqualTo("content".getBytes());
    }

    @Test(expected = OutOfMemoryError.class)
    @Ignore
    public void readAllBytesOutOfMemory() throws IOException {
        final Path file = newTempDir().resolve("file.big");
        final RandomAccessFile f = new RandomAccessFile(file.toFile(),
                                                        "rw");
        f.setLength(Integer.MAX_VALUE + 1L);

        f.close();

        Files.readAllBytes(file);
    }

    @Test(expected = NoSuchFileException.class)
    public void readAllBytesFileNotExists() {
        Files.readAllBytes(newTempDir().resolve("file.big"));
    }

    @Test(expected = NoSuchFileException.class)
    public void readAllBytesDir() {
        Files.readAllBytes(newTempDir());
    }

    @Test(expected = IllegalArgumentException.class)
    public void readAllBytesNull() {
        Files.readAllBytes(null);
    }

    @Test
    public void readAllLines() throws IOException {
        final Path dir = newTempDir();
        final BufferedWriter writer = Files.newBufferedWriter(dir.resolve("myfile.txt"),
                                                              Charset.defaultCharset());
        assertThat(writer).isNotNull();
        writer.write("content");
        writer.close();

        final List<String> result = Files.readAllLines(dir.resolve("myfile.txt"),
                                                       Charset.defaultCharset());
        assertThat(result).hasSize(1)
                .contains("content", atIndex(0));

        final BufferedWriter writer2 = Files.newBufferedWriter(dir.resolve("myfile2.txt"),
                                                               Charset.defaultCharset());
        assertThat(writer2).isNotNull();
        writer2.write("content\nnewFile\nline");
        writer2.close();

        final List<String> result2 = Files.readAllLines(dir.resolve("myfile2.txt"),
                                                        Charset.defaultCharset());
        assertThat(result2).hasSize(3)
                .contains("content", atIndex(0))
                .contains("newFile", atIndex(1))
                .contains("line", atIndex(2));
    }

    @Test(expected = NoSuchFileException.class)
    public void readAllLinesFileNotExists() {
        Files.readAllLines(newTempDir().resolve("file.big"),
                           Charset.defaultCharset());
    }

    @Test(expected = NoSuchFileException.class)
    public void readAllLinesDir() {
        Files.readAllLines(newTempDir(),
                           Charset.defaultCharset());
    }

    @Test(expected = IllegalArgumentException.class)
    public void readAllLinesNull1() {
        Files.readAllLines(null,
                           Charset.defaultCharset());
    }

    @Test(expected = IllegalArgumentException.class)
    public void readAllLinesNull2() {
        Files.readAllLines(Files.createTempFile(null,
                                                null),
                           null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readAllLinesNull3() {
        Files.readAllLines(null,
                           null);
    }

    @Test
    public void write() {
        final Path dir = newTempDir();

        Files.write(dir.resolve("file.txt"),
                    "content".getBytes());
        assertThat(Files.readAllBytes(dir.resolve("file.txt"))).hasSize("content".getBytes().length).isEqualTo("content".getBytes());
    }

    @Test(expected = org.uberfire.java.nio.IOException.class)
    public void writeDir() {
        Files.write(newTempDir(),
                    "content".getBytes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void writeNull1() {
        Files.write(newTempDir().resolve("file.txt"),
                    null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void writeNull2() {
        Files.write(null,
                    "".getBytes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void writeNull3() {
        Files.write(null,
                    null);
    }

    @Test
    public void writeLines() {
        final Path dir = newTempDir();
        final List<String> content = new ArrayList<String>() {{
            add("some");
            add("value");
        }};

        Files.write(dir.resolve("file.txt"),
                    content,
                    Charset.defaultCharset());

        final List<String> result = Files.readAllLines(dir.resolve("file.txt"),
                                                       Charset.defaultCharset());
        assertThat(result).hasSize(2)
                .contains("some", atIndex(0))
                .contains("value", atIndex(1));
    }

    @Test(expected = org.uberfire.java.nio.IOException.class)
    public void writeLinesDir() {
        final List<String> content = new ArrayList<String>() {{
            add("some");
            add("value");
        }};

        Files.write(newTempDir(),
                    content,
                    Charset.defaultCharset());
    }

    @Test(expected = IllegalArgumentException.class)
    public void writeLinesNull1() {
        Files.write(newTempDir().resolve("file.txt"),
                    null,
                    Charset.defaultCharset());
    }

    @Test(expected = IllegalArgumentException.class)
    public void writeLinesNull2() {
        final List<String> content = new ArrayList<String>();
        Files.write(null,
                    content,
                    Charset.defaultCharset());
    }

    @Test(expected = IllegalArgumentException.class)
    public void writeLinesNull4() {
        final List<String> content = new ArrayList<String>();
        Files.write(newTempDir().resolve("file.txt"),
                    content,
                    null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void writeLinesNull5() {
        final List<String> content = new ArrayList<String>();
        Files.write(null,
                    null,
                    null);
    }
}
