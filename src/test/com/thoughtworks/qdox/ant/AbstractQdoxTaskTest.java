package com.thoughtworks.qdox.ant;

import junit.framework.TestCase;

import java.io.File;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FileSet;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.DocletTagFactory;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.DefaultDocletTag;

// Not really abstract, but a test of the abstract.

public final class AbstractQdoxTaskTest extends TestCase {

    public AbstractQdoxTaskTest(String name) {
        super(name);
    }

    public void testBasic() throws Exception {
        OveriddenAbstractQdoxTask task = new OveriddenAbstractQdoxTask();
        task.addFileset(new OveriddenFileSet("src/java", new String[]{"com/thoughtworks/qdox/directorywalker/SuffixFilter.java"}));
        task.execute();

        JavaClass hopefullySuffixFilter = ((JavaClass) task.allClasses.get(0));

        assertNotNull("Expected a JavaClass", task.allClasses.get(0));
        assertEquals("SuffixFilter", hopefullySuffixFilter.getName());
        assertEquals("com.thoughtworks.qdox.directorywalker.SuffixFilter", task.classes);
    }

    public void testPluggableTagFactory() {
        OwnTagFactoryUsingQdoxTask task = new OwnTagFactoryUsingQdoxTask();
        task.execute();
    }

    private class OwnTagFactoryUsingQdoxTask extends AbstractQdoxTask {

        public OwnTagFactoryUsingQdoxTask() {
            addFileset(new OveriddenFileSet("src/test", new String[]{"com/thoughtworks/qdox/testdata/PropertyClass.java"}));
        }

        protected DocletTagFactory createDocletTagFactory() {
            // Tag factory that returns tags with "aslak." prefixed to their "original" name.
            // Not useful at all, only to test that we can actually plug in any tag factory.
            return new DocletTagFactory() {
                public DocletTag createDocletTag(String tag, String text) {
                    return new DefaultDocletTag( "aslak." + tag, text  );
                }
            };
        }

        public void execute() {
            super.execute();

            JavaClass hopefullyPropertyClass = (JavaClass) allClasses.get(0);
            DocletTag hopefullyAslakDotFoo = hopefullyPropertyClass.getTagByName("aslak.foo");
            assertNotNull(hopefullyAslakDotFoo);
            assertEquals("zap", hopefullyAslakDotFoo.getNamedParameter("bar"));
        }
    }

    public void testNoFileSets() {
        OveriddenAbstractQdoxTask task = new OveriddenAbstractQdoxTask();
        try {
            task.execute();
            fail("Expected an empty list of classes");
        } catch (BuildException e) {
            // expected
        }
    }

    private class OveriddenAbstractQdoxTask extends AbstractQdoxTask {
        public String classes = "";

        public void execute() {
            super.execute();

            for (int i = 0; i < allClasses.size(); i++) {
                JavaClass javaClass = (JavaClass) allClasses.get(i);
                classes = classes + javaClass.getFullyQualifiedName();
                // Interested in seeing output? Uncomment this.
                //        System.out.println("Class:" + javaClass.getName());
            }
        }
    }

    private class OveriddenFileSet extends FileSet {
        private OveridenDirectoryScanner overidenDirectoryScanner;
        private String dir;

        public OveriddenFileSet(String dir, String[] includedFiles) {
            this.dir = dir;
            overidenDirectoryScanner = new OveridenDirectoryScanner(includedFiles);
        }

        public File getDir(Project project) {
            // TODO What is this for? It affects coverage without results. PH
            //try {
            //    FileOutputStream fos = new FileOutputStream("build/ZZZZ");
            //    fos.write(12);
            //    fos.close();
            //} catch (IOException e) {
            //    e.printStackTrace();
            //}
            return getUnderJUnitFile(dir);
        }

        public DirectoryScanner getDirectoryScanner(Project project) {
            return overidenDirectoryScanner;
        }
    }

    public static File getUnderJUnitFile(String filename) {
        File result = new File("../" + filename);
        if (result.exists()) return result;
        return new File(filename);
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
