package com.thoughtworks.qdox.traversal;

import junit.framework.TestCase;

import java.io.File;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.ant.ConsoleLoggingQdoxTask;
import com.thoughtworks.qdox.ant.AbstractQdoxTask;

public class TraversalTest extends TestCase {

    private JavaDocBuilder builder;

    public TraversalTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception {
        File javaSrc = new File("../src/java");
        if (!javaSrc.exists()) {
            javaSrc = new File("src/java");
        }
        builder = new JavaDocBuilder();
        builder.addSourceTree(javaSrc);
    }

    public void testParentClassTraversal() throws Exception {
        JavaClass javaClass = builder.getClassByName(ConsoleLoggingQdoxTask.class.getName());
        JavaClass parentClass = javaClass.getSuperJavaClass();
        assertNotNull("Parent class not found", parentClass);
        assertEquals("Parent class traversal has returned the wrong parent",
                AbstractQdoxTask.class.getName(),
                parentClass.getFullyQualifiedName());

    }

}
