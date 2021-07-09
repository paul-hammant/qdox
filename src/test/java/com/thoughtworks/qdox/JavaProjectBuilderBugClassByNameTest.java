package com.thoughtworks.qdox;

import com.thoughtworks.qdox.model.JavaClass;
import junit.framework.TestCase;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JavaProjectBuilderBugClassByNameTest extends TestCase {

    private JavaProjectBuilder builder;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        builder = new JavaProjectBuilder();

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Removing one comment (no line starting with some spaces),
     * one class has a space at the end of his name.
     * @throws IOException
     */
    public void testBugWhenLineWithoutSpacesAtThe() throws IOException {
        List<String> lines = readFile("src/test/resources/com/thoughtworks/qdox/testdata/CodeExtractorTest.java");

        String outputFilename = "target/CodeExtractorTestOutput.java";

        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename));
        for (String line : lines) {
            // Remove one specific line
            if (!line.contains("// XXXX")) {
                writer.write(line);
                writer.write("\n");
            }
        }
        writer.close();

        checkSource(outputFilename);
    }

    private List<String> readFile(String filename) throws IOException {
        final File file = new File(filename);
        List<String> lines = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        try {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } finally {
            br.close();
        }
        return lines;
    }


    public void testCodeExtractorBug() throws IOException {
        checkSource("src/test/resources/com/thoughtworks/qdox/testdata/CodeExtractorTest.java");
    }

    private void checkSource(String pathname) throws IOException {
        builder.addSource(new File(pathname));

        final JavaClass unknownClass = builder.getClassByName("org.sfvl.doctesting.utils.CodeExtractorXXXXTest");
        assertEquals(0, unknownClass.getLineNumber());

        final JavaClass classByName = builder.getClassByName("org.sfvl.doctesting.utils.CodeExtractorTest");
        assertTrue(0 != classByName.getLineNumber());

        // Sometime name is "org.sfvl.doctesting.utils.CodeExtractorTest$ExtractCode$ExtractPartOfCode " (with  a space at the end)
        final JavaClass nestedClass = builder.getClassByName("org.sfvl.doctesting.utils.CodeExtractorTest$ExtractCode$ExtractPartOfCode");
        assertTrue(0 != nestedClass.getLineNumber());
    }


}