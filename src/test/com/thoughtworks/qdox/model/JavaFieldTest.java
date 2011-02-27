package com.thoughtworks.qdox.model;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public abstract class JavaFieldTest<F extends JavaField> extends TestCase {

    public JavaFieldTest(String s) {
        super(s);
    }
    
    //constructors
    public abstract F newJavaField();
    public abstract F newJavaField(Type type, String name);
    
    //setters
    public abstract void setComment(F fld, String comment);
    public abstract void setInitializationExpression(F fld, String expression);
    public abstract void setModifiers(F fld, List<String> modifiers);
    public abstract void setName(F fld, String name);
    public abstract void setType(F fld, Type type);
    
    public abstract Type newType(String fullname);
    public abstract Type newType(String fullname, int dimensions);
    public abstract JavaClass newJavaClass(String fullname);
    public abstract JavaPackage newJavaPackage(String name);
    
    
    public abstract void addField(JavaClass clazz, JavaField fld);
    public abstract void addClass(JavaPackage pckg, JavaClass clazz);

    public void testGetCodeBlock() throws Exception {
        F fld = newJavaField();
        setName(fld, "count");
        setType(fld, newType("int"));
        assertEquals("int count;\n", fld.getCodeBlock());
    }

    public void testGetCodeBlockWithModifiers() throws Exception {
        F fld = newJavaField();
        setName(fld, "count");
        setType(fld, newType("int"));
        setModifiers(fld, Arrays.asList(new String[]{"public", "final"}));
        assertEquals("public final int count;\n", fld.getCodeBlock());
    }

    public void testGetCodeBlockWithComment() throws Exception {
        F fld = newJavaField();
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
        F fld = newJavaField();
        setName(fld, "count");
        setType(fld, newType("int", 1));
        String expected = "int[] count;\n";
        assertEquals(expected, fld.getCodeBlock());
    }

    public void testGetCodeBlock2dArray() throws Exception {
        F fld = newJavaField();
        setName(fld, "count");
        setType(fld, newType("int", 2));
        String expected = "int[][] count;\n";
        assertEquals(expected, fld.getCodeBlock());
    }

    public void testGetCodeBlockWithValue() throws Exception {
        F fld = newJavaField();
        setName(fld, "stuff");
        setType(fld, newType("String"));
        setInitializationExpression(fld, "STUFF + getThing()");
        String expected = "String stuff = STUFF + getThing();\n";
        assertEquals(expected, fld.getCodeBlock());
    }
    
    public void testShouldReturnFieldNameForCallSignature() throws Exception {
        F fld = newJavaField();
        setName(fld, "count");
        setType(fld, newType("int"));
        setModifiers(fld, Arrays.asList(new String[]{"public", "final"}));
        assertEquals("count", fld.getCallSignature());
    }

    public void testShouldReturnProperDeclarationSignatureWithModifiers() throws Exception {
        F fld = newJavaField();
        setName(fld, "count");
        setType(fld, newType("int"));
        setModifiers(fld, Arrays.asList(new String[]{"public", "final"}));
        assertEquals("public final int count", fld.getDeclarationSignature(true));
    }

    public void testShouldReturnProperDeclarationSignatureWithoutModifiers() throws Exception {
        F fld = newJavaField();
        setName(fld, "count");
        setType(fld, newType("int"));
        setModifiers(fld, Arrays.asList(new String[]{"public", "final"}));
        assertEquals("int count", fld.getDeclarationSignature(false));
    }
    
    public void testToStringThreadMIN_PRIORITY() throws Exception {
    	JavaClass cls = newJavaClass("java.lang.Thread");
    	F fld = newJavaField(newType("int"), "MIN_PRIORITY");
    	setModifiers(fld, Arrays.asList(new String[] {"final", "static", "public"}));
    	addField(cls, fld);
    	assertEquals("public static final int java.lang.Thread.MIN_PRIORITY", fld.toString());
    }
    
    public void testToStringFieldDescriptorFd() throws Exception {
    	JavaPackage pckg =  newJavaPackage("java.io");
    	JavaClass cls = newJavaClass("FileDescriptor");
    	addClass(pckg, cls);
    	F fld =  newJavaField(newType("int"), "fd");
    	setModifiers(fld, Arrays.asList(new String[]{"private"}));
    	addField(cls, fld);
    	assertEquals("private int java.io.FileDescriptor.fd", fld.toString());
    }
}
