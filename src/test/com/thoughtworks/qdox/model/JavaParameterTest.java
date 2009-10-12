package com.thoughtworks.qdox.model;

import junit.framework.TestCase;

public class JavaParameterTest extends TestCase {

    public JavaParameterTest(String s) {
        super(s);
    }

    public void testParentMethod() throws Exception {
        JavaParameter p = new JavaParameter(new Type("x"), "x");
        assertNull(p.getParentMethod());

        JavaMethod m = new JavaMethod();
        m.addParameter(p);
        assertSame(m, p.getParentMethod());
    }

}
