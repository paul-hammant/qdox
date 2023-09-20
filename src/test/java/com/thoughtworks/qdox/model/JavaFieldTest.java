package com.thoughtworks.qdox.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class JavaFieldTest<F extends JavaField> {

    //constructors
    public abstract F newJavaField();
    public abstract F newJavaField(JavaClass type, String name);
    
    //setters
    public abstract void setComment(F fld, String comment);
    public abstract void setInitializationExpression(F fld, String expression);
    public abstract void setModifiers(F fld, List<String> modifiers);
    public abstract void setName(F fld, String name);
    public abstract void setType(F fld, JavaClass type);
    public abstract void setDeclaringClass(F fld, JavaClass cls);
    
    public JavaClass newJavaClass( String fullname )
    {
        return newJavaClass( fullname, 0 );
    }

    public JavaClass newJavaClass(String fullname, int dimensions) 
    {
        JavaClass result = mock( JavaClass.class );
        when( result.getFullyQualifiedName() ).thenReturn( fullname );
        String canonicalName = fullname.replace( '$', '.' );
        when( result.getValue() ).thenReturn( canonicalName );
        when( result.getDimensions()).thenReturn( dimensions );
        for(int i = 0; i < dimensions; i++)
        {
            canonicalName += "[]";
        }
        when( result.getCanonicalName() ).thenReturn( canonicalName );
        when( result.getGenericCanonicalName() ).thenReturn( canonicalName );
        return result;
    }

    @Test
    public void testHashCode()
    {
        Assertions.assertTrue(newJavaField().hashCode() != 0, "hashCode should never resolve to 0");
    }

    @Test
    public void testGetCodeBlock() {
        F fld = newJavaField();
        setName(fld, "count");
        setType(fld, newJavaClass("int"));
        Assertions.assertEquals("int count;\n", fld.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockWithModifiers() {
        F fld = newJavaField();
        setName(fld, "count");
        setType(fld, newJavaClass("int"));
        setModifiers(fld, Arrays.asList(new String[]{"public", "final"}));
        Assertions.assertEquals("public final int count;\n", fld.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockWithComment() {
        F fld = newJavaField();
        setName(fld, "count");
        setType(fld, newJavaClass("int"));
        setComment(fld, "Hello");
        String expected = ""
                + "/**\n"
                + " * Hello\n"
                + " */\n"
                + "int count;\n";
        Assertions.assertEquals(expected, fld.getCodeBlock());
    }

    @Test
    public void testGetCodeBlock1dArray() {
        F fld = newJavaField();
        setName(fld, "count");
        setType(fld, newJavaClass("int", 1));
        String expected = "int[] count;\n";
        Assertions.assertEquals(expected, fld.getCodeBlock());
    }

    @Test
    public void testGetCodeBlock2dArray() {
        F fld = newJavaField();
        setName(fld, "count");
        setType(fld, newJavaClass("int", 2));
        String expected = "int[][] count;\n";
        Assertions.assertEquals(expected, fld.getCodeBlock());
    }

    @Test
    public void testGetCodeBlockWithValue() {
        F fld = newJavaField();
        setName(fld, "stuff");
        setType(fld, newJavaClass("String"));
        setInitializationExpression(fld, "STUFF + getThing()");
        String expected = "String stuff = STUFF + getThing();\n";
        Assertions.assertEquals(expected, fld.getCodeBlock());
    }

    @Test
    public void testToStringThreadMIN_PRIORITY() {
    	JavaClass cls = mock(JavaClass.class);
    	when(cls.getFullyQualifiedName()).thenReturn( "java.lang.Thread" );
    	F fld = newJavaField(newJavaClass("int"), "MIN_PRIORITY");
    	setModifiers(fld, Arrays.asList(new String[] {"final", "static", "public"}));
    	setDeclaringClass( fld, cls );
    	Assertions.assertEquals("public static final int java.lang.Thread.MIN_PRIORITY", fld.toString());
    }

    @Test
    public void testToStringFieldDescriptorFd() {
    	JavaClass cls = mock(JavaClass.class);
    	when(cls.getFullyQualifiedName()).thenReturn("java.io.FileDescriptor");
    	F fld =  newJavaField(newJavaClass("int"), "fd");
    	setModifiers(fld, Arrays.asList(new String[]{"private"}));
    	setDeclaringClass( fld, cls );
    	Assertions.assertEquals("private int java.io.FileDescriptor.fd", fld.toString());
    }

    @Test
    public void testIsPublic()
    {
        F fld = newJavaField();
        Assertions.assertTrue(!fld.isPublic());

        setModifiers( fld, Arrays.asList( new String[] { "public" } ) );
        Assertions.assertTrue(fld.isPublic());
    }

    @Test
    public void testIsProtected()
    {
        F fld = newJavaField();
        Assertions.assertTrue(!fld.isProtected());

        setModifiers( fld, Arrays.asList( new String[] { "protected" } ) );
        Assertions.assertTrue(fld.isProtected());
    }

    @Test
    public void testIsPrivate()
    {
        F fld = newJavaField();
        Assertions.assertTrue(!fld.isPrivate());

        setModifiers( fld, Arrays.asList( new String[] { "private" } ) );
        Assertions.assertTrue(fld.isPrivate());
    }

    @Test
    public void testIsAbstract()
    {
        F fld = newJavaField();
        Assertions.assertTrue(!fld.isAbstract());

        setModifiers( fld, Arrays.asList( new String[] { "abstract" } ) );
        Assertions.assertTrue(fld.isAbstract());
    }

    @Test
    public void testIsFinal()
    {
        F fld = newJavaField();
        Assertions.assertTrue(!fld.isFinal());

        setModifiers( fld, Arrays.asList( new String[] { "final" } ) );
        Assertions.assertTrue(fld.isFinal());
    }

    @Test
    public void testIsNavite()
    {
        F fld = newJavaField();
        Assertions.assertTrue(!fld.isNative());

        setModifiers( fld, Arrays.asList( new String[] { "native" } ) );
        Assertions.assertTrue(fld.isNative());
    }

    @Test
    public void testIsStatic()
    {
        F fld = newJavaField();
        Assertions.assertTrue(!fld.isStatic());

        setModifiers( fld, Arrays.asList( new String[] { "static" } ) );
        Assertions.assertTrue(fld.isStatic());
    }

    @Test
    public void testIsStrict()
    {
        F fld = newJavaField();
        Assertions.assertTrue(!fld.isStrictfp());

        setModifiers( fld, Arrays.asList( new String[] { "strictfp" } ) );
        Assertions.assertTrue(fld.isStrictfp());
    }

    @Test
    public void testIsSynchronized()
    {
        F fld = newJavaField();
        Assertions.assertTrue(!fld.isSynchronized());

        setModifiers( fld, Arrays.asList( new String[] { "synchronized" } ) );
        Assertions.assertTrue(fld.isSynchronized());
    }

    @Test
    public void testIsTransient()
    {
        F fld = newJavaField();
        Assertions.assertTrue(!fld.isTransient());

        setModifiers( fld, Arrays.asList( new String[] { "transient" } ) );
        Assertions.assertTrue(fld.isTransient());
    }

    @Test
    public void testIsVolatile()
    {
        F fld = newJavaField();
        Assertions.assertTrue(!fld.isVolatile());

        setModifiers( fld, Arrays.asList( new String[] { "volatile" } ) );
        Assertions.assertTrue(fld.isVolatile());
    }

    @Test
    public void testEquals()
    {
        JavaClass type = mock(JavaClass.class);
        JavaClass declaringClass = mock(JavaClass.class);
        F fld1 = newJavaField( type, "field");
        F fld2 = newJavaField( type, "field");
        setDeclaringClass( fld1, declaringClass );
        setDeclaringClass( fld2, declaringClass );
        
        Assertions.assertEquals(fld1, fld2);
    }
}