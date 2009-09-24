package com.thoughtworks.qdox.model;

import junit.framework.TestCase;

import com.thoughtworks.qdox.JavaClassContext;
import com.thoughtworks.qdox.JavaDocBuilder;

public class TypeTest extends TestCase {

    public TypeTest(String s) {
        super(s);
    }

    public void testResolving() throws Exception {
        ClassLibrary classLib = new ClassLibrary();
        JavaSource src = new JavaSource();
        src.setClassLibrary(classLib);
        src.addImport("foo.*");
        Type type = Type.createUnresolved("Bar", 0, src);
        assertEquals(false, type.isResolved());
        classLib.add("foo.Bar");
        assertEquals(true, type.isResolved());
        assertEquals("foo.Bar", type.getValue());
    }

    public void testArrayType() throws Exception {
        Type type = new Type("int", 1);
        assertTrue(type.isArray());
    }

    public void testToString() throws Exception {
        assertEquals("int", new Type("int").toString());
        assertEquals("int[]", new Type("int", 1).toString());
        assertEquals("long[][][]", new Type("long", 3).toString());
    }

    public void testEquals() throws Exception {
        assertEquals(new Type("string"),
                new Type("string"));
        assertNotEquals(new Type("string"),
                new Type("int"));
        assertNotEquals(new Type("long", 1),
                new Type("long"));
        assertNotEquals(new Type("long"),
                new Type("long", 2));
        assertFalse(new Type("int").equals(null));
    }

    public void testTypeHasJavaClass() {
        JavaSource javaSource = new JavaSource(new JavaClassContext(new JavaDocBuilder()));
        javaSource.setClassLibrary(new ClassLibrary());
        Type type = new Type("java.util.HashSet", 0, javaSource);
        JavaClass clazz = type.getJavaClass();
        JavaClass superClass = clazz.getSuperJavaClass();
        assertEquals("java.util.AbstractSet", superClass.getFullyQualifiedName());
    }

    public void testIsPrimitive() throws Exception {
    	ClassLibrary classLibrary = new ClassLibrary( ClassLoader.getSystemClassLoader() );
        JavaDocBuilder builder = new JavaDocBuilder(classLibrary);
        
        JavaClass javaClass = builder.getClassByName("java.lang.Object");
        assertEquals(true, javaClass.getMethodBySignature("notify", null).getReturns().isPrimitive());

    } 

    private void assertNotEquals(Object o1, Object o2) {
        assertTrue(o2.toString() + " should not equal " + o1.toString(),
                !o2.equals(o1));
    }

    
}
