package com.thoughtworks.qdox.model;

import junit.framework.TestCase;

public class JavaFieldTest extends TestCase {

    public JavaFieldTest(String s) {
        super(s);
    }

    public void testToString() throws Exception {
        JavaField fld = new JavaField();
        fld.setName("count");
        fld.setType(new Type("int"));
        assertEquals("int count;\n", fld.toString());
    }

    public void testToStringWithModifiers() throws Exception {
        JavaField fld = new JavaField();
        fld.setName("count");
        fld.setType(new Type("int"));
        fld.setModifiers(new String[]{"public", "final"});
        assertEquals("public final int count;\n", fld.toString());
    }

    public void testToStringWithComment() throws Exception {
        JavaField fld = new JavaField();
        fld.setName("count");
        fld.setType(new Type("int"));
        fld.setComment("Hello");
        String expected = ""
                + "/**\n"
                + " * Hello\n"
                + " */\n"
                + "int count;\n";
        assertEquals(expected, fld.toString());
    }

    public void testToString1dArray() throws Exception {
        JavaField fld = new JavaField();
        fld.setName("count");
        fld.setType(new Type("int", 1));
        String expected = "int[] count;\n";
        assertEquals(expected, fld.toString());
    }

    public void testToString2dArray() throws Exception {
        JavaField fld = new JavaField();
        fld.setName("count");
        fld.setType(new Type("int", 2));
        String expected = "int[][] count;\n";
        assertEquals(expected, fld.toString());
    }

    public void testToStringWithValue() throws Exception {
        JavaField fld = new JavaField();
        fld.setName("stuff");
        fld.setType(new Type("String"));
        fld.setInitializationExpression("STUFF + getThing()");
        String expected = "String stuff = STUFF + getThing();\n";
        assertEquals(expected, fld.toString());
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
}
