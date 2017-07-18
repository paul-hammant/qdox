package com.thoughtworks.qdox.tools;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.directorywalker.DirectoryScanner;
import com.thoughtworks.qdox.directorywalker.FileVisitor;
import com.thoughtworks.qdox.directorywalker.SuffixFilter;
import com.thoughtworks.qdox.parser.ParseException;

/**
 * Tool for testing that QDox can parse Java source code.
 *
 * @author Joe Walnes
 */
public class QDoxTester {

    public static interface Reporter {
        void success(String id);

        void parseFailure(String id, int line, int column, String reason);

        void error(String id, Throwable throwable);
    }

    private final Reporter reporter;

    public QDoxTester(Reporter reporter) {
        this.reporter = reporter;
    }

    public void checkZipOrJarFile(File file) throws IOException {
        ZipFile zipFile = new ZipFile(file);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = entries.nextElement();
            if( zipEntry.isDirectory() )
            {
                continue;
            }
            InputStream inputStream = zipFile.getInputStream(zipEntry);
            try {
                verify(file.getName() + "!" + zipEntry.getName(), inputStream);
            } finally {
                inputStream.close();
            }
        }
    }

    public void checkDirectory(File dir) {
        DirectoryScanner directoryScanner = new DirectoryScanner(dir);
        directoryScanner.addFilter(new SuffixFilter(".java"));
        directoryScanner.scan(new FileVisitor() {
            public void visitFile(File file) {
                try {
                    checkJavaFile(file);
                } catch (IOException e) {
                    // ?
                }
            }
        });
    }

    public void checkJavaFile(File file) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        try {
            verify(file.getName(), inputStream);
        } finally {
            inputStream.close();
        }
    }

    private void verify(String id, InputStream inputStream) {
        try {
            JavaProjectBuilder builder = new JavaProjectBuilder();
            builder.addSource(new BufferedReader(new InputStreamReader(inputStream)));
            reporter.success(id);
        } catch (ParseException parseException) {
            reporter.parseFailure(id, parseException.getLine(), parseException.getColumn(), parseException.getMessage());
        } catch (Exception otherException) {
            reporter.error(id, otherException);
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Tool that verifies that QDox can parse some Java source.");
            System.err.println();
            System.err.println("Usage: java " + QDoxTester.class.getName() + " src1 [src2] [src3]...");
            System.err.println();
            System.err.println("Each src can be a single .java file, or a directory/zip/jar containing multiple source files");
            System.exit(-1);
        }

        ConsoleReporter reporter = new ConsoleReporter(System.out);
        QDoxTester qDoxTester = new QDoxTester(reporter);
        for (int i = 0; i < args.length; i++) {
            File file = new File(args[i]);
            if (file.isDirectory()) {
                qDoxTester.checkDirectory(file);
            } else if (file.getName().endsWith(".java")) {
                qDoxTester.checkJavaFile(file);
            } else if (file.getName().endsWith(".jar") || file.getName().endsWith(".zip")) {
                qDoxTester.checkZipOrJarFile(file);
            } else {
                System.err.println("Unknown input <" + file.getName() + ">. Should be zip, jar, java or directory");
            }
        }
        reporter.writeSummary();
    }

    private static class ConsoleReporter implements Reporter {

        private final PrintStream out;

        private int success;
        private int failure;
        private int error;

        private int dotsWrittenThisLine;

        public ConsoleReporter(PrintStream out) {
            this.out = out;
        }

        public void success(String id) {
            success++;
            if (++dotsWrittenThisLine > 80) {
                newLine();
            }
            out.print('.');
        }

        private void newLine() {
            dotsWrittenThisLine = 0;
            out.println();
            out.flush();
        }

        public void parseFailure(String id, int line, int column, String reason) {
            newLine();
            out.println("* " + id);
            out.println("  [" + line + ":" + column + "] " + reason);
            failure++;
        }

        public void error(String id, Throwable throwable) {
            newLine();
            out.println("* " + id);
            throwable.printStackTrace(out);
            error++;
        }

        public void writeSummary() {
            newLine();
            out.println("-- Summary --------------");
            out.println("Success: " + success);
            out.println("Failure: " + failure);
            out.println("Error  : " + error);
            out.println("Total  : " + (success + failure + error));
            out.println("-------------------------");
        }

    }
}
