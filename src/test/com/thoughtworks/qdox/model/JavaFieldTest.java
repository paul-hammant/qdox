package com.thoughtworks.qdox.model;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public abstract class JavaFieldTest extends TestCase {

    public JavaFieldTest(String s) {
        super(s);
    }
    
    public abstract JavaField newJavaField();
    public abstract JavaField newJavaField(Type type, String name);
    public abstract Type newType(String fullname);
    public abstract Type newType(String fullname, int dimensions);
    public abstract JavaClass newJavaClass(String fullname);
    public abstract JavaPackage newJavaPackage(String name);
    
    public abstract void setComment(JavaField fld, String comment);
    public abstract void setInitializationExpression(JavaField fld, String expression);
    public abstract void setModifiers(JavaField fld, List<String> modifiers);
    public abstract void setName(JavaField fld, String name);
    public abstract void setType(JavaField fld, Type type);
    
    public abstract void addField(JavaClass clazz, JavaField fld);
    public abstract void addClass(JavaPackage pckg, JavaClass clazz);

    public void testGetCodeBlock() throws Exception {
        JavaField fld = newJavaField();
        setName(fld, "count");
        setType(fld, newType("int"));
        assertEquals("int count;\n", fld.getCodeBlock());
    }

    public void testGetCodeBlockWithModifiers() throws Exception {
        JavaField fld = newJavaField();
        setName(fld, "count");
        setType(fld, newType("int"));
        setModifiers(fld, Arrays.asList(new String[]{"public", "final"}));
        assertEquals("public final int count;\n", fld.getCodeBlock());
    }

    public void testGetCodeBlockWithComment() throws Exception {
        JavaField fld = newJavaField();
        setName(fld, "count");
        setType(fld, newType("int"));
        setComment(fld, "Hello");
        String expected = ""
                + "/**\n"
                + " * Hello\n"
                + " */\n"
                + "int count;\n";
        assertEquals(expected, fld.getCodeBlock());
    }

    public void testGetCodeBlock1dArray() throws Exception {
        JavaField fld = newJavaField();
        setName(fld, "count");
        setType(fld, newType("int", 1));
        String expected = "int[] count;\n";
        assertEquals(expected, fld.getCodeBlock());
    }

    public void testGetCodeBlock2dArray() throws Exception {
        JavaField fld = newJavaField();
        setName(fld, "count");
        setType(fld, newType("int", 2));
        String expected = "int[][] count;\n";
        assertEquals(expected, fld.getCodeBlock());
    }

    public void testGetCodeBlockWithValue() throws Exception {
        JavaField fld = newJavaField();
        setName(fld, "stuff");
        setType(fld, newType("String"));
        setInitializationExpression(fld, "STUFF + getThing()");
        String expected = "String stuff = STUFF + getThing();\n";
        assertEquals(expected, fld.getCodeBlock());
    }
    
    public void testShouldReturnFieldNameForCallSignature() throws Exception {
        JavaField fld = newJavaField();
        setName(fld, "count");
        setType(fld, newType("int"));
        setModifiers(fld, Arrays.asList(new String[]{"public", "final"}));
        assertEquals("count", fld.getCallSignature());
    }

    public void testShouldReturnProperDeclarationSignatureWithModifiers() throws Exception {
        JavaField fld = newJavaField();
        setName(fld, "count");
        setType(fld, newType("int"));
        setModifiers(fld, Arrays.asList(new String[]{"public", "final"}));
        assertEquals("public final int count", fld.getDeclarationSignature(true));
    }

    public void testShouldReturnProperDeclarationSignatureWithoutModifiers() throws Exception {
        JavaField fld = newJavaField();
        setName(fld, "count");
        setType(fld, newType("int"));
        setModifiers(fld, Arrays.asList(new String[]{"public", "final"}));
        assertEquals("int count", fld.getDeclarationSignature(false));
    }
    
    public void testToStringThreadMIN_PRIORITY() throws Exception {
    	JavaClass cls = newJavaClass("java.lang.Thread");
    	JavaField fld = newJavaField(newType("int"), "MIN_PRIORITY");
    	setModifiers(fld, Arrays.asList(new String[] {"final", "static", "public"}));
    	addField(cls, fld);
    	assertEquals("public static final int java.lang.Thread.MIN_PRIORITY", fld.toString());
    }
    
    public void testToStringFieldDescriptorFd() throws Exception {
    	JavaPackage pckg =  newJavaPackage("java.io");
    	JavaClass cls = newJavaClass("FileDescriptor");
    	addClass(pckg, cls);
    	JavaField fld =  newJavaField(newType("int"), "fd");
    	setModifiers(fld, Arrays.asList(new String[]{"private"}));
    	addField(cls, fld);
    	assertEquals("private int java.io.FileDescriptor.fd", fld.toString());
    }
}
