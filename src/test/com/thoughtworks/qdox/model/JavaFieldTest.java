package com.thoughtworks.qdox.model;

import junit.framework.TestCase;

public class JavaFieldTest extends TestCase {

    public JavaFieldTest(String s) {
        super(s);
    }

    public void testGetCodeBlock() throws Exception {
        JavaField fld = new JavaField();
        fld.setName("count");
        fld.setType(new Type("int"));
        assertEquals("int count;\n", fld.getCodeBlock());
    }

    public void testGetCodeBlockWithModifiers() throws Exception {
        JavaField fld = new JavaField();
        fld.setName("count");
        fld.setType(new Type("int"));
        fld.setModifiers(new String[]{"public", "final"});
        assertEquals("public final int count;\n", fld.getCodeBlock());
    }

    public void testGetCodeBlockWithComment() throws Exception {
        JavaField fld = new JavaField();
        fld.setName("count");
        fld.setType(new Type("int"));
        fld.setComment("Hello");
        String expected = ""
                + "/**\n"
                + " * Hello\n"
                + " */\n"
                + "int count;\n";
        assertEquals(expected, fld.getCodeBlock());
    }

    public void testGetCodeBlock1dArray() throws Exception {
        JavaField fld = new JavaField();
        fld.setName("count");
        fld.setType(new Type("int", 1));
        String expected = "int[] count;\n";
        assertEquals(expected, fld.getCodeBlock());
    }

    public void testGetCodeBlock2dArray() throws Exception {
        JavaField fld = new JavaField();
        fld.setName("count");
        fld.setType(new Type("int", 2));
        String expected = "int[][] count;\n";
        assertEquals(expected, fld.getCodeBlock());
    }

    public void testGetCodeBlockWithValue() throws Exception {
        JavaField fld = new JavaField();
        fld.setName("stuff");
        fld.setType(new Type("String"));
        fld.setInitializationExpression("STUFF + getThing()");
        String expected = "String stuff = STUFF + getThing();\n";
        assertEquals(expected, fld.getCodeBlock());
    }
    
    public void testShouldReturnFieldNameForCallSignature() throws Exception {
        JavaField fld = new JavaField();
        fld.setName("count");
        fld.setType(new Type("int"));
        fld.setModifiers(new String[]{"public", "final"});
        assertEquals("count", fld.getCallSignature());
    }

    public void testShouldReturnProperDeclarationSignatureWithModifiers() throws Exception {
        JavaField fld = new JavaField();
        fld.setName("count");
        fld.setType(new Type("int"));
        fld.setModifiers(new String[]{"public", "final"});
        assertEquals("public final int count", fld.getDeclarationSignature(true));
    }

    public void testShouldReturnProperDeclarationSignatureWithoutModifiers() throws Exception {
        JavaField fld = new JavaField();
        fld.setName("count");
        fld.setType(new Type("int"));
        fld.setModifiers(new String[]{"public", "final"});
        assertEquals("int count", fld.getDeclarationSignature(false));
    }
    
    public void testToStringThreadMIN_PRIORITY() throws Exception {
    	JavaClass cls = new JavaClass("java.lang.Thread");
    	JavaField fld = new JavaField(new Type("int"), "MIN_PRIORITY");
    	fld.setModifiers(new String[] {"final", "static", "public"});
    	cls.addField(fld);
    	assertEquals("public static final int java.lang.Thread.MIN_PRIORITY", fld.toString());
    }
    
    public void testToStringFieldDescriptorFd() throws Exception {
    	JavaPackage pckg =  new JavaPackage("java.io");
    	JavaClass cls = new JavaClass("FileDescriptor");
    	pckg.addClass(cls);
    	JavaField fld =  new JavaField(new Type("int"), "fd");
    	fld.setModifiers(new String[]{"private"});
    	cls.addField(fld);
    	assertEquals("private int java.io.FileDescriptor.fd", fld.toString());
    }
}
