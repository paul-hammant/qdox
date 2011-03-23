package com.thoughtworks.qdox.model;

import java.io.StringReader;

import junit.framework.TestCase;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.library.ClassLoaderLibrary;

public abstract class TypeTest extends TestCase {

    public TypeTest(String s) {
        super(s);
    }
    
    public abstract JavaSource newJavaSource(com.thoughtworks.qdox.library.ClassLibrary library);
    public abstract Type newType(String fullname);
    public abstract Type newType(String fullname, int dimensions);
    public abstract Type newType(String fullname, int dimensions, JavaSource source);
    
    public abstract void addImport(JavaSource source, String imp);

    public void testResolving() throws Exception {
        ClassLibrary classLib = new ClassLibrary();
        JavaSource src = newJavaSource(classLib);
        addImport(src, "foo.*");
        Type type = Type.createUnresolved("Bar", 0, src);
        assertEquals(false, type.isResolved());
        classLib.getContext().add( new DefaultJavaClass("foo.Bar"));
        assertEquals(true, type.isResolved());
        assertEquals("foo.Bar", type.getValue());
    }

    public void testArrayType() throws Exception {
        Type type = newType("int", 1);
        assertTrue(type.isArray());
    }

    public void testToString() throws Exception {
        assertEquals("int", newType("int").toString());
        assertEquals("int[]", newType("int", 1).toString());
        assertEquals("long[][][]", newType("long", 3).toString());
    }

    public void testEquals() throws Exception {
        assertEquals(newType("string"),
                newType("string"));
        assertNotEquals(newType("string"),
                newType("int"));
        assertNotEquals(newType("long", 1),
                newType("long"));
        assertNotEquals(newType("long"),
                newType("long", 2));
        assertFalse(newType("int").equals(null));
    }

    public void testTypeHasJavaClass() {
        ClassLoaderLibrary library = new ClassLoaderLibrary( null );
        library.addDefaultLoader();
        JavaSource javaSource = newJavaSource(library);
        Type type = newType("java.util.HashSet", 0, javaSource);
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
    
    public void testInnerClassToString() {
        ClassLibrary classLibrary = new ClassLibrary( ClassLoader.getSystemClassLoader() );

        String source = "public class Outer {\n" +
            " private Inner ia;" +
            " public class Inner { }" +
            "}";
        JavaDocBuilder builder = new JavaDocBuilder(classLibrary);
        builder.addSource( new StringReader( source )  );
        assertEquals("Outer.Inner", builder.getClassByName( "Outer" ).getFieldByName( "ia" ).getType().toString());
        assertEquals("Outer.Inner", builder.getClassByName( "Outer" ).getFieldByName( "ia" ).getType().toGenericString());
    }
    
    public void testToStringVoid() {
        assertEquals("void", Type.VOID.toString());
    }

    public void testToStringBoolean() {
        assertEquals("boolean", newType("boolean").toString());
    }
    
    public void testToStringInt() {
        assertEquals("int", newType("int").toString());
    }

    public void testToStringLong() {
        assertEquals("long", newType("long").toString());
    }

    public void testToStringFloat() {
        assertEquals("float", newType("float").toString());
    }

    public void testToStringDouble() {
        assertEquals("double", newType("double").toString());
    }
    
    public void testToStringChar() {
        assertEquals("char", newType("char").toString());
    }

    public void testToStringByte() {
        assertEquals("byte", newType("byte").toString());
    }
    
    private void assertNotEquals(Object o1, Object o2) {
        assertTrue(o2.toString() + " should not equal " + o1.toString(),
                !o2.equals(o1));
    }
}
