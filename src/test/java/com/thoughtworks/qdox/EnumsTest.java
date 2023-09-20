package com.thoughtworks.qdox;

import com.thoughtworks.qdox.model.JavaClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

public class EnumsTest {

    @Test
    public void testAddEmptyEnumsToModel() {
        String source = ""
                + "public enum Enum1 {}\n"
                + "enum Enum2 {;}\n"
                + "private enum Enum3 {,}";

        JavaProjectBuilder javaDocBuilder = new JavaProjectBuilder();
        javaDocBuilder.addSource(new StringReader(source));

        Assertions.assertTrue(javaDocBuilder.getClassByName("Enum1").isEnum());
        Assertions.assertTrue(javaDocBuilder.getClassByName("Enum2").isEnum());
        Assertions.assertTrue(javaDocBuilder.getClassByName("Enum3").isEnum());
    }

    @Test
    public void testAddSimpleEnumsToModel() {

        String source = ""
                + "public enum Enum1 { a, b }"
                + "class X { "
                + "  enum Enum2 { c, /** some doc */ d } "
                + "  int someField; "
                + "}";

        JavaProjectBuilder javaDocBuilder = new JavaProjectBuilder();
        javaDocBuilder.addSource(new StringReader(source));

        JavaClass cls = javaDocBuilder.getClassByName("X");
        Assertions.assertEquals("int", cls.getFieldByName("someField").getType().getValue()); // sanity check
        Assertions.assertTrue(javaDocBuilder.getClassByName("Enum1").isEnum());
        Assertions.assertTrue(javaDocBuilder.getClassByName("X$Enum2").isEnum());
    }

    @Test
    public void testAddEnumImplementingInterfaceToModel() {
        String source = ""
                + "public enum Enum1 implements java.io.Serializable { a, b }";

        JavaProjectBuilder javaDocBuilder = new JavaProjectBuilder();
        javaDocBuilder.addSource(new StringReader(source));

        JavaClass cls = javaDocBuilder.getClassByName("Enum1");
        Assertions.assertTrue(cls.isEnum());
        Assertions.assertTrue(cls.isA("java.io.Serializable"));
    }

    @Test
    public void testAddEnumWithAnnotationToModel() {
        String source = ""
                + "public enum Enum1 implements java.io.Serializable { a, @Deprecated b }";

        JavaProjectBuilder javaDocBuilder = new JavaProjectBuilder();
        javaDocBuilder.addSource(new StringReader(source));

        JavaClass cls = javaDocBuilder.getClassByName("Enum1");
        Assertions.assertTrue(cls.isEnum());
        Assertions.assertTrue(cls.isA("java.io.Serializable"));
    }

    @Test
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

        JavaProjectBuilder javaDocBuilder = new JavaProjectBuilder();
        javaDocBuilder.addSource(new StringReader(source));

        JavaClass cls = javaDocBuilder.getClassByName("X$EnumWithConstructors");
        Assertions.assertTrue(cls.isEnum());
        Assertions.assertEquals(3, cls.getFields().size());
        Assertions.assertEquals(2, cls.getConstructors().size());
        Assertions.assertEquals("int", cls.getFieldByName("someField").getType().getValue()); // sanity check
        Assertions.assertTrue(cls.getFieldByName( "c" ).isEnumConstant(), "c should be recognized as a enumConstant");
        Assertions.assertTrue(cls.getFieldByName( "d" ).isEnumConstant(), "d should be recognized as a enumConstant");
        Assertions.assertFalse(cls.getFieldByName( "someField" ).isEnumConstant());
    }

    @Test
    public void testAddEnumsWithMethodsToModel() {
        String source = ""
                + "public enum Animal {\n"
                + "    \n"
                + "    DUCK    { public void speak() { System.out.println(\"quack!\"); } },\n"
                + "    CHICKEN { public void speak() { System.out.println(\"cluck!\"); } };\n"
                + "\n"
                + "    public abstract void speak();\n"
                + "}";

        JavaProjectBuilder javaDocBuilder = new JavaProjectBuilder();
        javaDocBuilder.addSource(new StringReader(source));

        Assertions.assertTrue(javaDocBuilder.getClassByName("Animal").isEnum());
    }

    //Verify test case from QDOX-74
    @Test
    public void testAddEnumsWithConstructorsToModel() {
        String source = ""
                + "public enum AccountType {\n"
                + "    \n"
                + "    ADMINISTRATOR (1, \"Administrator\"),\n"
                + "    CUSTOMER (2, \"Customer\"),\n"
                + "\n"
                + "}";

        JavaProjectBuilder javaDocBuilder = new JavaProjectBuilder();
        javaDocBuilder.addSource(new StringReader(source));

        Assertions.assertTrue(javaDocBuilder.getClassByName("AccountType").isEnum());
    }
    
    //Verify test case from QDOX-74
    @Test
    public void testAddEnumsThatDontEndInSemicolon() {
        String source = ""
                + "public enum Foo { BAR }\n";

        JavaProjectBuilder javaDocBuilder = new JavaProjectBuilder();
        javaDocBuilder.addSource(new StringReader(source));

        Assertions.assertTrue(javaDocBuilder.getClassByName("Foo").isEnum());
    }


    @Test
    public void testEnumBeforeClass() {
        String source = "" +
        	"package org.carrot2.util.attribute.constraint;" +
        	"public class Test" +
        	"{" +
        	"public enum TestValueSet" +
        	"{ VALUE_1 }" +
        	"static class AnnotationContainer" +
        	"{ @ValueHintEnum(values = TestValueSet.class) String hint; }" +
        	"}";
        new JavaProjectBuilder().addSource(new StringReader(source));
   }

    @Test
    public void testEnumAfterClass() {
        String source = "" +
        	"package org.carrot2.util.attribute.constraint;" +
        	"public class Test" +
        	"{" +
        	"static class AnnotationContainer" +
        	"{ @ValueHintEnum(values = TestValueSet.class) String hint; }" +
        	"public enum TestValueSet" +
        	"{ VALUE_1 }" +
        	"}";
        new JavaProjectBuilder().addSource(new StringReader(source));
   }
    
    //for QDOX-153
    @Test
    public void testAnotherEnumTest() {
    	String source = "package org.apache.myfaces.el.unified.resolver;\n" +
    			"public final class FacesCompositeELResolver extends org.apache.myfaces.el.CompositeELResolver\n" +
    			"{\n" +
    			" public enum Scope\n" +
    			" { Faces, JSP }\n" +
    			" public FacesCompositeELResolver(final Scope scope) {}\n" +
    			"}";
    	
    	new JavaProjectBuilder().addSource(new StringReader(source));
     }
    
    // QDOX-240
    @Test
    public void testObjectCreation()
    {
        String source="package simpleenum;\r\n" + 
        		"import java.util.Date;\r\n" + 
        		"public enum MinimalEnumExampleConstructor\r\n" + 
        		"{\r\n" + 
        		"  D_CONSTRUCTOR(new Date());         // FAILS to be parsed\r\n" + 
        		"  private final Date date;\r\n" + 
        		"  private MinimalEnumExampleConstructor(final Date date)\r\n" + 
        		"  {\r\n" + 
        		"    this.date = date;\r\n" + 
        		"  }\r\n" + 
        		"}";
        new JavaProjectBuilder().addSource(new StringReader(source));
    }
    
    // QDOX-240
    @Test
    public void testMethodInvocation()
    {
        String source="package simpleenum;\r\n" + 
        		"import java.util.Date;\r\n" + 
        		"public enum MinimalEnumExampleMethod\r\n" + 
        		"{\r\n" + 
        		"  D_METHOD(create());            // FAILS to be parsed\r\n" + 
        		"  private final Date date;\r\n" + 
        		"  private MinimalEnumExampleMethod(final Date date)\r\n" + 
        		"  {\r\n" + 
        		"    this.date = date;\r\n" + 
        		"  }\r\n" + 
        		"  public static Date create()\r\n" + 
        		"  {\r\n" + 
        		"    return new Date();\r\n" + 
        		"  }\r\n" + 
        		"}";
        new JavaProjectBuilder().addSource(new StringReader(source));
    }
    
}
