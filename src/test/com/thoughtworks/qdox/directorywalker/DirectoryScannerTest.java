package com.thoughtworks.qdox.directorywalker;

import java.io.File;
import java.util.List;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

public class DirectoryScannerTest extends MockObjectTestCase {

    class MockFile extends File {
        boolean isDirectory;
        File[] children;

        public MockFile(String pathname) {
            super(pathname);
            this.isDirectory = false;
        }

        public MockFile(String pathname, boolean isDirectory) {
            super(pathname);
            this.isDirectory = isDirectory;
        }

        public boolean isDirectory() {
            return this.isDirectory;
        }

        public File[] listFiles() {
            return children;
        }
    }

    public DirectoryScannerTest(String s) {
        super(s);
    }

    public void testSingleDirectory() {
        MockFile rootDir = new MockFile("root", true);
        rootDir.children = new File[]{new MockFile("blah.txt"), new MockFile("foo.txt"), new MockFile("pig.java")};
        DirectoryScanner scanner = new DirectoryScanner(rootDir);
        List<File> files = scanner.scan();
        assertEquals(3, files.size());
        assertEquals("blah.txt", files.get(0).getName());
        assertEquals("foo.txt", files.get(1).getName());
        assertEquals("pig.java", files.get(2).getName());
    }

    public void testDirectoryWithSubdir() {
        MockFile rootDir = new MockFile("root", true);
        MockFile subDir = new MockFile("subdir", true);
        subDir.children = new File[]{new MockFile("child1.txt"), new MockFile("child2.txt")};
        rootDir.children = new File[]{subDir, new MockFile("foo.txt"), new MockFile("pig.java")};
        DirectoryScanner scanner = new DirectoryScanner(rootDir);
        List<File> files = scanner.scan();
        assertEquals(4, files.size());
        assertEquals("child1.txt", files.get(0).getName());
        assertEquals("child2.txt", files.get(1).getName());
        assertEquals("foo.txt", files.get(2).getName());
        assertEquals("pig.java", files.get(3).getName());
    }

    public void testDirectoryWithSubdirWithSubdir() {
        MockFile rootDir = new MockFile("root", true);
        MockFile subDir1 = new MockFile("subdir", true);
        MockFile subDir2 = new MockFile("subdir2", true);
        subDir2.children = new File[]{new MockFile("grandChild1.txt")};
        subDir1.children = new File[]{subDir2, new MockFile("child1.txt"), new MockFile("child2.txt")};
        rootDir.children = new File[]{subDir1, new MockFile("foo.txt"), new MockFile("pig.java")};

        DirectoryScanner scanner = new DirectoryScanner(rootDir);
        List<File> files = scanner.scan();
        assertEquals(5, files.size());
        assertEquals("grandChild1.txt", files.get(0).getName());
        assertEquals("child1.txt", files.get(1).getName());
        assertEquals("child2.txt", files.get(2).getName());
        assertEquals("foo.txt", files.get(3).getName());
        assertEquals("pig.java", files.get(4).getName());
    }

    public void testSuffixFilter() {
        MockFile rootDir = new MockFile("root", true);
        rootDir.children = new File[]{new MockFile("blah.txt"), new MockFile("foo.java"), new MockFile("pig.java")};
        DirectoryScanner scanner = new DirectoryScanner(rootDir);
        scanner.addFilter(new SuffixFilter(".java"));
        List<File> files = scanner.scan();
        assertEquals(2, files.size());
        assertEquals("foo.java", files.get(0).getName());
        assertEquals("pig.java", files.get(1).getName());
    }

    public void testFilterCallback() {
        MockFile rootDir = new MockFile("root", true);
        rootDir.children = new File[]{new MockFile("blah.txt"), new MockFile("foo.java"), new MockFile("pig.java")};
        DirectoryScanner scanner = new DirectoryScanner(rootDir);
        Filter mockFilter = new Filter() {
            public boolean filter(File file) {
                return file.getName().equals("foo.java");
            }
        };
        scanner.addFilter(mockFilter);
        List<File> files = scanner.scan();
        assertEquals(1, files.size());
        assertEquals("foo.java", files.get(0).getName());
    }

    public void testMultipleFilters() {
        MockFile rootDir = new MockFile("root", true);
        rootDir.children = new File[]{new MockFile("blah.txt"), new MockFile("foo.java"),
                                      new MockFile("pig.java"), new MockFile("foo.txt")};
        DirectoryScanner scanner = new DirectoryScanner(rootDir);
        scanner.addFilter(new SuffixFilter(".java"));
        scanner.addFilter(new Filter() {
            public boolean filter(File file) {
                return file.getName().startsWith("foo");
            }
        });
        List<File> files = scanner.scan();
        assertEquals(1, files.size());
        assertEquals("foo.java", files.get(0).getName());
    }

    public void testFileVisitor() {
        MockFile rootDir = new MockFile("root", true);
        rootDir.children = new File[]{new MockFile("blah.txt"), new MockFile("foo.txt"), new MockFile("pig.java")};
        DirectoryScanner scanner = new DirectoryScanner(rootDir);
        Mock mockFileVisitor = new Mock(FileVisitor.class);
        mockFileVisitor.expects(once()).method("visitFile").with(same(rootDir.children[0]));
        mockFileVisitor.expects(once()).method("visitFile").with(same(rootDir.children[1]));
        mockFileVisitor.expects(once()).method("visitFile").with(same(rootDir.children[2]));

        scanner.scan((FileVisitor) mockFileVisitor.proxy());

        mockFileVisitor.verify();

    }
}
