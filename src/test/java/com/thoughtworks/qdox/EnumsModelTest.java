package com.thoughtworks.qdox;

import java.io.File;
import java.io.StringReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;

import junit.framework.TestCase;


public class EnumsModelTest extends TestCase {

    // These tests verify that we can access enum fields in the model.
	// This is a sequel to EnumsTest.java.

    public void testAddEmptyEnumsToModel() {

        String source = ""
                + "public enum Enum1 {}\n"
                + "enum Enum2 {;}\n";

        JavaProjectBuilder javaDocBuilder = new JavaProjectBuilder();
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

        JavaProjectBuilder javaDocBuilder = new JavaProjectBuilder();
        javaDocBuilder.addSource(new StringReader(source));

        JavaClass cls = javaDocBuilder.getClassByName("X");
        assertEquals("int", cls.getFieldByName("someField").getType().getValue()); // sanity check
        JavaClass enum1 = javaDocBuilder.getClassByName("Enum1");
        assertTrue(enum1.isEnum());
        JavaClass enum2 = javaDocBuilder.getClassByName("X$Enum2");
        assertTrue(enum2.isEnum());
        
        //---
        
        List<JavaField> fields1 = enum1.getFields();
//      printFields( fields1 );
        assertEquals(2, fields1.size());
        
        JavaField enum1a = fields1.get(0);
        assertNull( enum1a.getComment() );
        assertEquals( 1, enum1a.getModifiers().size() );
        assertEquals( "public", enum1a.getModifiers().get(0) );
        assertEquals( 0, enum1a.getAnnotations().size() );
        assertEquals( "Enum1", enum1a.getType().toString() );
        assertEquals( "a", enum1a.getName() );
        
        //---

        List<JavaField> fields2 = enum2.getFields();
//      printFields( fields2 );
        assertEquals(2, fields2.size());
        
        JavaField enum2d = fields2.get(1);
        assertNotNull( enum2d.getComment() );
        assertEquals( 0, enum2d.getModifiers().size() );
        assertEquals( 0, enum2d.getAnnotations().size() );
        assertEquals( "X$Enum2", enum2d.getType().getBinaryName() );
        assertEquals( "X.Enum2", enum2d.getType().getFullyQualifiedName() );
        assertEquals( "d", enum2d.getName() );

        //---
    }
    
    public void testAddEnumImplementingInterfaceToModel() {
        String source = ""
                + "public enum Enum1 implements java.io.Serializable { a, b }";

        JavaProjectBuilder javaDocBuilder = new JavaProjectBuilder();
        javaDocBuilder.addSource(new StringReader(source));

        JavaClass cls = javaDocBuilder.getClassByName("Enum1");
        assertTrue(cls.isEnum());
        assertTrue(cls.isA("java.io.Serializable"));
    }

    public void testAddEnumWithAnnotationToModel() {
        String source = ""
                + "public enum Enum1 implements java.io.Serializable { a, @Deprecated b }";

        JavaProjectBuilder javaDocBuilder = new JavaProjectBuilder();
        javaDocBuilder.addSource(new StringReader(source));

        JavaClass cls = javaDocBuilder.getClassByName("Enum1");
        assertTrue(cls.isEnum());
        assertTrue(cls.isA("java.io.Serializable"));
        
        //---

        List<JavaField> fields = cls.getFields();
//      printFields( fields );
        assertEquals(2, fields.size());
        
        JavaField enum1b = fields.get(1);
        assertNull( enum1b.getComment() );
        assertEquals( 1, enum1b.getModifiers().size() );
        assertEquals( "public", enum1b.getModifiers().get(0) );
        assertEquals( 1, enum1b.getAnnotations().size() );
        assertEquals( "@java.lang.Deprecated()", enum1b.getAnnotations().get(0).toString() );
        assertEquals( "Enum1", enum1b.getType().toString() );
        assertEquals( "b", enum1b.getName() );

        //---
    }
    
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
        assertTrue(cls.isEnum());
        assertEquals("int", cls.getFieldByName("someField").getType().getValue()); // sanity check
        
        //---

        List<JavaField> fields = cls.getFields();
//      printFields( fields );
        assertEquals(3, fields.size());		// includes c, d, and someField
        
        JavaField enum1c = fields.get(0);
        assertNull( enum1c.getComment() );
        assertEquals( 0, enum1c.getModifiers().size() );
        assertEquals( 0, enum1c.getAnnotations().size() );
        assertEquals( "X$EnumWithConstructors", enum1c.getType().getBinaryName() );
        assertEquals( "X.EnumWithConstructors", enum1c.getType().getFullyQualifiedName() );
        assertEquals( "c", enum1c.getName() );

        //---
        
        JavaField enum1d = fields.get(1);
        assertNull( enum1d.getComment() );
        assertEquals( 0, enum1d.getModifiers().size() );
        assertEquals( "X$EnumWithConstructors", enum1d.getType().getBinaryName() );
        assertEquals( "X.EnumWithConstructors", enum1d.getType().getFullyQualifiedName() );
        assertEquals( "d", enum1d.getName() );

        //---
    }
    
    public void testAddEnumWithFieldAndConstructorsToModelBinary() throws Exception
    {
        JavaProjectBuilder javaDocBuilder = new JavaProjectBuilder();
        javaDocBuilder.addClassLoader( new URLClassLoader( new URL[] { new File("target/test-classes/").toURI().toURL() } )  );
        
        JavaClass cls = javaDocBuilder.getClassByName("X$EnumWithConstructors");
        assertTrue(cls.isEnum());
        assertEquals("int", cls.getFieldByName("someField").getType().getValue()); // sanity check
        
        //---

        List<JavaField> fields = cls.getFields();
        
        JavaField enum1c = fields.get(0);
        assertNull( enum1c.getComment() );
        assertEquals( 0, enum1c.getAnnotations().size() );
        assertEquals( "X$EnumWithConstructors", enum1c.getType().getBinaryName() );
        assertEquals( "X.EnumWithConstructors", enum1c.getType().getFullyQualifiedName() );
        assertEquals( "c", enum1c.getName() );

        //---
        
        JavaField enum1d = fields.get(1);
        assertNull( enum1d.getComment() );
        assertEquals( "X$EnumWithConstructors", enum1d.getType().getBinaryName() );
        assertEquals( "X.EnumWithConstructors", enum1d.getType().getFullyQualifiedName() );
        assertEquals( "d", enum1d.getName() );

        //---
    }

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
        assertTrue(cls.isEnum());
    }
    
    //for qdox-118 
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