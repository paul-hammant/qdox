package com.thoughtworks.qdox.traversal;

import junit.framework.TestCase;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.impl.DefaultJavaExecutable;
import com.thoughtworks.qdox.model.impl.DefaultJavaMethod;

public class TraversalTest extends TestCase {

    private JavaProjectBuilder builder;

    public TraversalTest(String name) {
        super(name);
    }

    @Override
	protected void setUp() throws Exception {
        builder = new JavaProjectBuilder();
        //by default current classloader is already added, so QDox-classes can be found
    }

    public void testParentClassTraversal() {
        JavaClass javaClass = builder.getClassByName(DefaultJavaMethod.class.getName());
        JavaClass parentClass = javaClass.getSuperJavaClass();
        assertNotNull("Parent class not found", parentClass);
        assertEquals("Parent class traversal has returned the wrong parent",
                DefaultJavaExecutable.class.getName(),
                parentClass.getFullyQualifiedName());

    }

}
