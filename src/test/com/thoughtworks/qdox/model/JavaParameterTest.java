package com.thoughtworks.qdox.model;

import junit.framework.TestCase;

public abstract class JavaParameterTest<P extends JavaParameter> extends TestCase {

    public JavaParameterTest(String s) {
        super(s);
    }
    
    //constructors
    public abstract P newJavaParameter(Type type, String name);
    
    //setters
    public abstract void setMethod(P parameter, JavaMethod method);

    
    public abstract Type newType(String typeName);
    public abstract JavaMethod newJavaMethod();
    

    public void testParentMethod() throws Exception {
        P p = newJavaParameter(newType("x"), "x");
        assertNull(p.getParentMethod());

        JavaMethod m = newJavaMethod();
        setMethod( p, m );
        assertSame(m, p.getParentMethod());
    }

}
