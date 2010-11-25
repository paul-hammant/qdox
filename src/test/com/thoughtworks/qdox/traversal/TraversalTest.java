package com.thoughtworks.qdox.traversal;

import junit.framework.TestCase;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.AbstractInheritableJavaEntity;
import com.thoughtworks.qdox.model.DefaultJavaMethod;
import com.thoughtworks.qdox.model.JavaClass;

public class TraversalTest extends TestCase {

    private JavaDocBuilder builder;

    public TraversalTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        builder = new JavaDocBuilder();
        //by default current classloader is already added, so QDox-classes can be found
    }

    public void testParentClassTraversal() throws Exception {
        JavaClass javaClass = builder.getClassByName(DefaultJavaMethod.class.getName());
        JavaClass parentClass = javaClass.getSuperJavaClass();
        assertNotNull("Parent class not found", parentClass);
        assertEquals("Parent class traversal has returned the wrong parent",
                AbstractInheritableJavaEntity.class.getName(),
                parentClass.getFullyQualifiedName());

    }

}
