package com.thoughtworks.qdox.traversal;

import junit.framework.TestCase;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.AbstractBaseMethod;
import com.thoughtworks.qdox.model.AbstractInheritableJavaEntity;
import com.thoughtworks.qdox.model.DefaultJavaMethod;
import com.thoughtworks.qdox.model.JavaClass;

public class TraversalTest extends TestCase {

    private JavaProjectBuilder builder;

    public TraversalTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        builder = new JavaProjectBuilder();
        //by default current classloader is already added, so QDox-classes can be found
    }

    public void testParentClassTraversal() throws Exception {
        JavaClass javaClass = builder.getClassByName(DefaultJavaMethod.class.getName());
        JavaClass parentClass = javaClass.getSuperJavaClass();
        assertNotNull("Parent class not found", parentClass);
        assertEquals("Parent class traversal has returned the wrong parent",
                AbstractBaseMethod.class.getName(),
                parentClass.getFullyQualifiedName());

    }

}
