package com.thoughtworks.qdox.model;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class TypeTest extends TestCase{

    public TypeTest(String s) {
        super(s);
    }

    public void testResolving() throws Exception {
		ClassLibrary classLib = new ClassLibrary();
		JavaSource src = new JavaSource();
		src.setClassLibrary(classLib);
		src.setImports( new String[] { "foo.*" } );
        Type type = new Type("Bar", 0, src);
        assertEquals(false, type.isResolved());
		classLib.add("foo.Bar");
        assertEquals(true, type.isResolved());
        assertEquals("foo.Bar", type.getValue());
    }

    public void testArrayType() throws Exception {
        Type type = new Type("int",1);
    	assertTrue(type.isArray());
    }

}
