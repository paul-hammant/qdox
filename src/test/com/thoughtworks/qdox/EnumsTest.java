package com.thoughtworks.qdox;

import junit.framework.TestCase;

import java.io.StringReader;

import com.thoughtworks.qdox.model.JavaClass;

public class EnumsTest extends TestCase {

    // NOTE: these tests verify that we can parse enum classes and that they are represented as
    // classes in the model.
    // Later versions of QDox will actually expose each enum value in the model: See QDOX-79

    public void testAddEmptyEnumsToModel() {

        String source = ""
                + "public enum Enum1 {}\n"
                + "enum Enum2 {;}\n";

        JavaDocBuilder javaDocBuilder = new JavaDocBuilder();
        javaDocBuilder.addSource(new StringReader(source));

        JavaClass enum1 = javaDocBuilder.getClassByName("Enum1");
        assertTrue(enum1.isEnum());
        JavaClass enum2 = javaDocBuilder.getClassByName("Enum2");
        assertTrue(enum2.isEnum());
    }

    public void testAddSimpleEnumsToModel() {

        String source = ""
                + "public enum Enum1 { a, b }"
                + "class X { "
                + "  enum Enum2 { c, /** some doc */ d } "
                + "  int someField; "
                + "}";

        JavaDocBuilder javaDocBuilder = new JavaDocBuilder();
        javaDocBuilder.addSource(new StringReader(source));

        JavaClass cls = javaDocBuilder.getClassByName("X");
        assertEquals("int", cls.getFieldByName("someField").getType().getValue()); // sanity check
        JavaClass enum1 = javaDocBuilder.getClassByName("Enum1");
        assertTrue(enum1.isEnum());
        JavaClass enum2 = javaDocBuilder.getClassByName("X$Enum2");
        assertTrue(enum2.isEnum());
    }
    
    public void testAddEnumWithFieldAndConstructorsToModel() {

        String source = ""
                + "class X {\n"
                + "    enum EnumWithConstructors {\n"
                + "        c(\"hello\"), d();\n"
                + "\n"
                + "        int someField;\n"
                + "\n"
                + "        EnumWithConstructors() {}\n"
                + "\n"
                + "        EnumWithConstructors(String x) {\n"
                + "        }\n"
                + "    }\n"
                + "}";

        JavaDocBuilder javaDocBuilder = new JavaDocBuilder();
        javaDocBuilder.addSource(new StringReader(source));

        JavaClass cls = javaDocBuilder.getClassByName("X$EnumWithConstructors");
        assertTrue(cls.isEnum());
        assertEquals("int", cls.getFieldByName("someField").getType().getValue()); // sanity check
    }

    public void testAddEnumsWithMethodsToModel() throws Exception {
        String source = ""
                + "public enum Animal {\n"
                + "    \n"
                + "    DUCK { public void speak() { System.out.println(\"quack!\"); } },\n"
                + "    CHICKEN { public void speak() { System.out.println(\"cluck!\"); } };\n"
                + "\n"
                + "    public abstract void speak();\n"
                + "}";

        JavaDocBuilder javaDocBuilder = new JavaDocBuilder();
        javaDocBuilder.addSource(new StringReader(source));

        JavaClass cls = javaDocBuilder.getClassByName("Animal");
        assertTrue(cls.isEnum());
    }

}
