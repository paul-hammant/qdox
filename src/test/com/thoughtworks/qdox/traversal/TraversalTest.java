package com.thoughtworks.qdox.traversal;

import junit.framework.TestCase;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.AbstractInheritableJavaEntity;
import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.ant.AbstractQdoxTaskTest;

public class TraversalTest extends TestCase {

    private JavaDocBuilder builder;

    public TraversalTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        builder = new JavaDocBuilder();
        builder.addSourceTree(AbstractQdoxTaskTest.getUnderJUnitFile("src/java"));
    }

    public void testParentClassTraversal() throws Exception {
        JavaClass javaClass = builder.getClassByName(JavaMethod.class.getName());
        JavaClass parentClass = javaClass.getSuperJavaClass();
        assertNotNull("Parent class not found", parentClass);
        assertEquals("Parent class traversal has returned the wrong parent",
                AbstractInheritableJavaEntity.class.getName(),
                parentClass.getFullyQualifiedName());

    }

}
