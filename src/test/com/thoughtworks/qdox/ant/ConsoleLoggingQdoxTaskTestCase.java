package com.thoughtworks.qdox.ant;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;
import com.thoughtworks.qdox.model.JavaClass;

public class ConsoleLoggingQdoxTaskTestCase extends TestCase
{

    public ConsoleLoggingQdoxTaskTestCase(String name)
    {
        super(name);
    }

    public void testBasic() throws Exception {
        AbstractQdoxTask task = new OverriddenConsoleLoggingQdoxTask();
        task.addFileset(new OveriddenFileSet(new String[] {"com/thoughtworks/qdox/directorywalker/SuffixFilter.java"}));
        task.execute();

        // fix me!

        assertNotNull("Expected a JavaClass", task.allClasses.get(0));
        assertEquals("SuffixFilter", ((JavaClass) task.allClasses.get(0)).getName());
    }

    private class OverriddenConsoleLoggingQdoxTask extends ConsoleLoggingQdoxTask {
        protected void printClassNames() {
            // do nothing.
        }
    }

    private class OveriddenFileSet extends FileSet {
        private OveridenDirectoryScanner overidenDirectoryScanner;

        public OveriddenFileSet(String[] includedFiles) {
            overidenDirectoryScanner = new OveridenDirectoryScanner(includedFiles);
        }

        public File getDir(Project project) {
            try {
                FileOutputStream fos = new FileOutputStream("ZZZZ");
                fos.write(12);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new File("../src/java");
        }

        public DirectoryScanner getDirectoryScanner(Project project) {
            return overidenDirectoryScanner;
        }
    }

    private class OveridenDirectoryScanner extends DirectoryScanner {
        private String[] includedFiles;

        public OveridenDirectoryScanner(String[] includedFiles) {
            this.includedFiles = includedFiles;
        }

        public String[] getIncludedFiles() {
            return includedFiles;
        }
    }
}
