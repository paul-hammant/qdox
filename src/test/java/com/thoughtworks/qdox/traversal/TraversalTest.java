package com.thoughtworks.qdox.traversal;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.impl.DefaultJavaExecutable;
import com.thoughtworks.qdox.model.impl.DefaultJavaMethod;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TraversalTest {

    private JavaProjectBuilder builder;

    @BeforeEach
    public void setUp() throws Exception {
        builder = new JavaProjectBuilder();
        //by default current classloader is already added, so QDox-classes can be found
    }

    @Test
    public void testParentClassTraversal() {
        JavaClass javaClass = builder.getClassByName(DefaultJavaMethod.class.getName());
        JavaClass parentClass = javaClass.getSuperJavaClass();
        Assertions.assertNotNull(parentClass, "Parent class not found");
        Assertions.assertEquals(DefaultJavaExecutable.class.getName(), parentClass.getFullyQualifiedName(), "Parent class traversal has returned the wrong parent");

    }

}
