package com.thoughtworks.qdox;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.StringReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;


public class EnumsModelTest {

    // These tests verify that we can access enum fields in the model.
	// This is a sequel to EnumsTest.java.

    @Test
    public void testAddEmptyEnumsToModel() {

        String source = ""
                + "public enum Enum1 {}\n"
                + "enum Enum2 {;}\n";

        JavaProjectBuilder javaDocBuilder = new JavaProjectBuilder();
        javaDocBuilder.addSource(new StringReader(source));

        JavaClass enum1 = javaDocBuilder.getClassByName("Enum1");
        Assertions.assertTrue(enum1.isEnum());
        JavaClass enum2 = javaDocBuilder.getClassByName("Enum2");
        Assertions.assertTrue(enum2.isEnum());
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
        JavaClass enum1 = javaDocBuilder.getClassByName("Enum1");
        Assertions.assertTrue(enum1.isEnum());
        JavaClass enum2 = javaDocBuilder.getClassByName("X$Enum2");
        Assertions.assertTrue(enum2.isEnum());
        
        //---
        
        List<JavaField> fields1 = enum1.getFields();
//      printFields( fields1 );
        Assertions.assertEquals(2, fields1.size());
        
        JavaField enum1a = fields1.get(0);
        Assertions.assertNull(enum1a.getComment());
        Assertions.assertEquals(1, enum1a.getModifiers().size());
        Assertions.assertEquals("public", enum1a.getModifiers().get(0));
        Assertions.assertEquals(0, enum1a.getAnnotations().size());
        Assertions.assertEquals("Enum1", enum1a.getType().toString());
        Assertions.assertEquals("a", enum1a.getName());
        
        //---

        List<JavaField> fields2 = enum2.getFields();
//      printFields( fields2 );
        Assertions.assertEquals(2, fields2.size());
        
        JavaField enum2d = fields2.get(1);
        Assertions.assertNotNull(enum2d.getComment());
        Assertions.assertEquals(0, enum2d.getModifiers().size());
        Assertions.assertEquals(0, enum2d.getAnnotations().size());
        Assertions.assertEquals("X$Enum2", enum2d.getType().getBinaryName());
        Assertions.assertEquals("X.Enum2", enum2d.getType().getFullyQualifiedName());
        Assertions.assertEquals("d", enum2d.getName());

        //---
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
        
        //---

        List<JavaField> fields = cls.getFields();
//      printFields( fields );
        Assertions.assertEquals(2, fields.size());
        
        JavaField enum1b = fields.get(1);
        Assertions.assertNull(enum1b.getComment());
        Assertions.assertEquals(1, enum1b.getModifiers().size());
        Assertions.assertEquals("public", enum1b.getModifiers().get(0));
        Assertions.assertEquals(1, enum1b.getAnnotations().size());
        Assertions.assertEquals("@java.lang.Deprecated()", enum1b.getAnnotations().get(0).toString());
        Assertions.assertEquals("Enum1", enum1b.getType().toString());
        Assertions.assertEquals("b", enum1b.getName());

        //---
    }

    @Test
    public void testAddEnumWithFieldAndConstructorsToModelSource() {

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
        Assertions.assertEquals("int", cls.getFieldByName("someField").getType().getValue()); // sanity check
        
        //---

        List<JavaField> fields = cls.getFields();
//      printFields( fields );
        Assertions.assertEquals(3, fields.size());		// includes c, d, and someField
        
        JavaField enum1c = fields.get(0);
        Assertions.assertNull(enum1c.getComment());
        Assertions.assertEquals(0, enum1c.getModifiers().size());
        Assertions.assertEquals(0, enum1c.getAnnotations().size());
        Assertions.assertEquals("X$EnumWithConstructors", enum1c.getType().getBinaryName());
        Assertions.assertEquals("X.EnumWithConstructors", enum1c.getType().getFullyQualifiedName());
        Assertions.assertEquals("c", enum1c.getName());

        //---
        
        JavaField enum1d = fields.get(1);
        Assertions.assertNull(enum1d.getComment());
        Assertions.assertEquals(0, enum1d.getModifiers().size());
        Assertions.assertEquals("X$EnumWithConstructors", enum1d.getType().getBinaryName());
        Assertions.assertEquals("X.EnumWithConstructors", enum1d.getType().getFullyQualifiedName());
        Assertions.assertEquals("d", enum1d.getName());

        //---
    }

    @Test
    public void testAddEnumWithFieldAndConstructorsToModelBinary() throws Exception
    {
        JavaProjectBuilder javaDocBuilder = new JavaProjectBuilder();
        javaDocBuilder.addClassLoader( new URLClassLoader( new URL[] { new File("target/test-classes/").toURI().toURL() } )  );
        
        JavaClass cls = javaDocBuilder.getClassByName("X$EnumWithConstructors");
        Assertions.assertTrue(cls.isEnum());
        Assertions.assertEquals("int", cls.getFieldByName("someField").getType().getValue()); // sanity check
        
        //---

        List<JavaField> fields = cls.getFields();
        
        JavaField enum1c = fields.get(0);
        Assertions.assertNull(enum1c.getComment());
        Assertions.assertEquals(0, enum1c.getAnnotations().size());
        Assertions.assertEquals("X$EnumWithConstructors", enum1c.getType().getBinaryName());
        Assertions.assertEquals("X.EnumWithConstructors", enum1c.getType().getFullyQualifiedName());
        Assertions.assertEquals("c", enum1c.getName());

        //---
        
        JavaField enum1d = fields.get(1);
        Assertions.assertNull(enum1d.getComment());
        Assertions.assertEquals("X$EnumWithConstructors", enum1d.getType().getBinaryName());
        Assertions.assertEquals("X.EnumWithConstructors", enum1d.getType().getFullyQualifiedName());
        Assertions.assertEquals("d", enum1d.getName());

        //---
    }

    @Test
    public void testAddEnumsWithMethodsToModel() {
        String source = ""
                + "public enum Animal {\n"
                + "    \n"
                + "    DUCK { public void speak() { System.out.println(\"quack!\"); } },\n"
                + "    CHICKEN { public void speak() { System.out.println(\"cluck!\"); } };\n"
                + "\n"
                + "    public abstract void speak();\n"
                + "}";

        JavaProjectBuilder javaDocBuilder = new JavaProjectBuilder();
        javaDocBuilder.addSource(new StringReader(source));

        JavaClass cls = javaDocBuilder.getClassByName("Animal");
        Assertions.assertTrue(cls.isEnum());
    }
    
    //for qdox-118 
    @Test
    public void testEnumWithJavaDocAndAnnotation() {
        String source = "public enum TestEnum\n" +
        		"{\n" +
        		"/**\n" +
        		"* Blah blah\n" +
        		"*/\n" +
        		"@MyAnno\n" +
        		"TEST;\n" +
        		"}\n";
        JavaProjectBuilder javaDocBuilder = new JavaProjectBuilder();
        javaDocBuilder.addSource(new StringReader(source));
    }
    
/*
    private void printFields(final JavaField fields[]) {
	    
    	for(int i=0; i<fields.length; i++) {
	    
    		final JavaField field = fields[i];
	    	
    		System.err.println( "\tcomment: " + field.getComment() );
	    	
	    	System.err.print( "\tmodifiers: " );
	    	for(int j=0; j<field.getModifiers().length; j++) {
	    		System.err.print( field.getModifiers()[j] + " " );
	    	}
	    	System.err.println();
	    	
	    	System.err.print( "\tannotations: " );
	    	for(int k=0; k<field.getAnnotations().length; k++) {
	    		System.err.print( field.getAnnotations()[k] + " " );
	    	}
	    	System.err.println();
	
	    	System.err.println( "\ttype: " + field.getType() );
	    	
	    	System.err.println( "\tname: " + field.getName() );
	    	
	    	System.err.println();
	    }
    }
*/
}