package com.thoughtworks.qdox.model;

import junit.framework.TestCase;

public abstract class JavaParameterTest extends TestCase {

    public JavaParameterTest(String s) {
        super(s);
    }
    
    public abstract Type newType(String typeName);
    
    public abstract JavaParameter newJavaParameter(Type type, String name);
    
    public abstract JavaMethod newJavaMethod();
    
    public abstract void addParameter(JavaMethod method, JavaParameter parameter);

    public void testParentMethod() throws Exception {
        JavaParameter p = newJavaParameter(newType("x"), "x");
        assertNull(p.getParentMethod());

        JavaMethod m = newJavaMethod();
        addParameter(m, p);
        assertSame(m, p.getParentMethod());
    }

}
